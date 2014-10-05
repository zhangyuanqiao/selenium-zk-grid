/*
Copyright 2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

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

import org.openqa.selenium.WebElement;
import shaded.org.openqa.selenium.remote.server.Session;

public abstract class WebElementHandler<T> extends WebDriverHandler<T> {
  private volatile String elementId;

  protected WebElementHandler(Session session) {
    super(session);
  }

  public void setId(String elementId) {
    this.elementId = elementId;
  }

  protected WebElement getElement() {
    return getKnownElements().get(elementId);
  }

  protected String getElementId() {
    return elementId;
  }

  protected String getElementAsString() {
    try {
      return elementId + " " + String.valueOf(getElement());
    } catch (RuntimeException e) {
      // Be paranoid!
    }

    return elementId + " unknown";
  }
}