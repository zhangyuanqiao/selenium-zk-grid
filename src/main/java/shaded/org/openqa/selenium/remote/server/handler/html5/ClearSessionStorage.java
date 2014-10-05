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

public class ClearSessionStorage extends shaded.org.openqa.selenium.remote.server.handler.WebDriverHandler<Void> {

  public ClearSessionStorage(shaded.org.openqa.selenium.remote.server.Session session) {
    super(session);
  }

  @Override
  public Void call() throws Exception {
    Utils.getWebStorage(getUnwrappedDriver()).getSessionStorage().clear();
    return null;
  }

  @Override
  public String toString() {
    return "[clear session storage]";
  }
}
