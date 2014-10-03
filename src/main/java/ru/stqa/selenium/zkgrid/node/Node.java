package ru.stqa.selenium.zkgrid.node;

import com.google.common.collect.Maps;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.server.DefaultDriverSessions;
import org.openqa.selenium.remote.server.JsonHttpCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.stqa.selenium.zkgrid.common.Curator;
import ru.stqa.selenium.zkgrid.common.SlotInfo;
import ru.stqa.selenium.zkgrid.common.StringSerializer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static ru.stqa.selenium.zkgrid.common.PathUtils.*;

public class Node {

  private static Logger log = LoggerFactory.getLogger(Node.class);

  private String nodeId = UUID.randomUUID().toString();
  private Map<String, SlotInfo> slots = Maps.newHashMap();

  private DefaultDriverSessions sessions;

  private final Curator curator;

  private ScheduledExecutorService serviceExecutor;

  private boolean heartBeating = true;
  private CommandHandler commandHandler;

  public static void main(String[] args) throws Exception {
    Node node = new Node("localhost:4444");
    node.start();
  }

  public Node(String connectionString) {
    curator = Curator.createCurator(connectionString);
  }

  private void start() throws Exception {
    sessions = new DefaultDriverSessions();
    commandHandler = new CommandHandler(sessions);

    registerToHub();

    serviceExecutor = Executors.newSingleThreadScheduledExecutor();
    serviceExecutor.scheduleAtFixedRate(new HeartBeat(), 1, 1, TimeUnit.SECONDS);
  }

  private void registerToHub() throws Exception {
    registerNode();
    registerSlots();
  }

  private void registerNode() throws Exception {
    curator.create(nodePath(nodeId));
    DistributedBarrier barrier = curator.createBarrier(nodePath(nodeId));

    DistributedQueue<String> queue = QueueBuilder.builder(
        curator.getClient(), null, new StringSerializer(), "/registrationRequests").buildQueue();
    queue.start();
    queue.put(nodeId);

    if (!barrier.waitOnBarrier(10, TimeUnit.SECONDS)) {
      throw new Error("Node can't register itself");
    }
  }

  private void registerSlots() throws Exception {
    for (int i = 0; i < 10; i++) {
      SlotInfo slot = new SlotInfo(nodeId, "" + i, DesiredCapabilities.firefox());
      curator.setData(nodeSlotPath(slot), new BeanToJsonConverter().convert(slot.getCapabilities()));
      startCommandListener(slot);
    }
  }

  private void startCommandListener(final SlotInfo slot) throws Exception {
    final NodeCache nodeCache = new NodeCache(curator.getClient(), nodeSlotCommandPath(slot), false);
    nodeCache.start();

    NodeCacheListener nodesListener = new NodeCacheListener () {
      @Override
      public void nodeChanged() throws Exception {
        String data = new String(nodeCache.getCurrentData().getData());
        Command cmd = new JsonToBeanConverter().convert(Command.class, data);
        log.info("Command received " + cmd);
        log.info("Dispatched to slot " + slot);
        Response res = commandHandler.handleCommand(cmd);
        curator.setData(nodeSlotResponsePath(slot), new BeanToJsonConverter().convert(res));
        curator.clearBarrier(nodeSlotPath(slot));
      }
    };

    nodeCache.getListenable().addListener(nodesListener);
  }

  private void unregisterFromHub() throws Exception {
    heartBeating = false;
    curator.delete(nodePath(nodeId));
  }

  private class HeartBeat implements Runnable {
    @Override
    public void run() {
      if (heartBeating) {
        try {
          curator.setData(nodeHeartBeatPath(nodeId), String.valueOf(System.currentTimeMillis()));
        } catch (Exception e) {
          heartBeating = false;
          e.printStackTrace();
        }
      }
    }
  }
}
