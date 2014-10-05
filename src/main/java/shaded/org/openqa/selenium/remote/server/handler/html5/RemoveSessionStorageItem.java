/*
Copyright 2010 Selenium committers

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

package shaded.org.openqa.selenium.remote.server.handler.html5;

public class RemoveSessionStorageItem extends shaded.org.openqa.selenium.remote.server.handler.WebDriverHandler<String> {
  private volatile String key;

  public RemoveSessionStorageItem(shaded.org.openqa.selenium.remote.server.Session session) {
    super(session);
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String call() throws Exception {
    return Utils.getWebStorage(getUnwrappedDriver())
        .getSessionStorage().removeItem(key);
  }

  @Override
  public String toString() {
    return String.format("[remove session storage item for key: %s]", key);
  }
}