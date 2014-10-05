/*
Copyright 2007-2009 Selenium committers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package shaded.org.openqa.selenium.remote.server.handler;

import shaded.org.openqa.selenium.remote.server.JsonParametersAware;
import shaded.org.openqa.selenium.remote.server.Session;

import java.util.Map;

public class SwitchToWindow extends WebDriverHandler<Void> implements JsonParametersAware {

  private volatile String name;

  public SwitchToWindow(Session session) {
    super(session);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setJsonParameters(Map<String, Object> allParameters) throws Exception {
    setName((String) allParameters.get("name"));
  }

  @Override
  public Void call() throws Exception {
    getDriver().switchTo().window(name);

    return null;
  }

  @Override
  public String toString() {
    return String.format("[switch to window: %s]", name);
  }
}
