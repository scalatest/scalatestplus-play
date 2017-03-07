/*
 * Copyright 2001-2016 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatestplus.play

import java.util.logging.Level

import com.machinepublishers.jbrowserdriver.Settings
import play.api.test._
import org.scalatest._
import org.scalatest.events._
import org.scalatest.tags._
import selenium.WebBrowser
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.{ FirefoxOptions, FirefoxProfile }
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Abstract class that encapsulates a browser name, tag name, and Selenium `WebDriver` factory method.
 *
 * This class is used by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]: an `IndexedSeq[BrowserInfo]` is returned
 * from the `browsers` field of these traits to specify the browsers to share between tests.
 * When tests are registered, `AllBrowsersPerSuite` and `AllBrowsersPerTest` use the browser name to ensure the tests shared by multiple browsers
 * have unique names (the name of each shared test is appended with a browser name). When the tests run, these traits
 * use the `BrowserInfo`'s factory method to create `WebDriver`s as needed.
 * The `AllBrowsersPerSuite` and `AllBrowsersPerTest` traits use the  tag name to automatically tag any tests that use
 * a particular `WebDriver` with the appropriate tag so that tests can be dynamically filtered by the browser the use.
 *
 * `BrowserInfo` is not sealed so that you can extend it if you need other Browser types, for example,
 * Firefox browsers with different profiles (English, Japanese, etc.).
 *
 * @param name the browser name, surrounded by square brackets
 * @param tagName the browser tag name
 */
abstract class BrowserInfo(val name: String, val tagName: String) {

  /**
   * Creates a new instance of a Selenium `WebDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicates the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `WebDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] if the desired
   * `WebDriver` was not available on the host platform.
   */
  def createWebDriver(): WebDriver
}

/**
 * Firefox browser info, which encapsulates the browser name, `"[Firefox]"`; tag name, `org.scalatest.tags.FirefoxBrowser`; and a factory method that produces a Selenium `FirefoxDriver`.
 *
 * This class's superclass, `BrowserInfo`, is used by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]: an `IndexedSeq[BrowserInfo]` is returned
 * from the `browsers` field of these traits to specify the browsers to share between tests.
 * When tests are registered, `AllBrowsersPerSuite` and `AllBrowsersPerTest` use the browser name to ensure the tests shared by multiple browsers
 * have unique names (the name of each shared test is appended with a browser name). When the tests run, these traits
 * use the `BrowserInfo`'s factory method to create `WebDriver`s as needed.
 * The `AllBrowsersPerSuite` and `AllBrowsersPerTest` traits use the  tag name to automatically tag any tests that use
 * a particular `WebDriver` with the appropriate tag so that tests can be dynamically filtered by the browser the use.
 *
 * @param firefoxProfile the `FirefoxProfile` to use when creating new `FirefoxDriver`s in the `createWebDriver` factory method.
 */
case class FirefoxInfo(firefoxProfile: FirefoxProfile, firefoxOptions: FirefoxOptions = new FirefoxOptions().setLogLevel(Level.WARNING)) extends BrowserInfo("[Firefox]", "org.scalatest.tags.FirefoxBrowser") {

  /**
   * Creates a new instance of a Selenium `FirefoxDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicates Firefox was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `FirefoxDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] if Firefox
   * was not available on the host platform.
   */
  def createWebDriver(): WebDriver = FirefoxFactory.createWebDriver(firefoxProfile, firefoxOptions)
}

/**
 * Safari browser info, which encapsulates the browser name, `"[Safari]"`; tag name, `org.scalatest.tags.SafariBrowser`; and a factory method that produces a Selenium `SafariDriver`.
 *
 * This object's superclass, `BrowserInfo`, is used by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]: an `IndexedSeq[BrowserInfo]` is returned
 * from the `browsers` field of these traits to specify the browsers to share between tests.
 * When tests are registered, `AllBrowsersPerSuite` and `AllBrowsersPerTest` use the browser name to ensure the tests shared by multiple browsers
 * have unique names (the name of each shared test is appended with a browser name). When the tests run, these traits
 * use the `BrowserInfo`'s factory method to create `WebDriver`s as needed.
 * The `AllBrowsersPerSuite` and `AllBrowsersPerTest` traits use the  tag name to automatically tag any tests that use
 * a particular `WebDriver` with the appropriate tag so that tests can be dynamically filtered by the browser the use.
 */
case class SafariInfo() extends BrowserInfo("[Safari]", "org.scalatest.tags.SafariBrowser") {

  /**
   * Creates a new instance of a Selenium `SafariDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicates Safari was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `SafariDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] if Safari
   * was not available on the host platform.
   */
  def createWebDriver(): WebDriver = SafariFactory.createWebDriver()
}

/**
 * Internet Explorer browser info, which encapsulates the browser name, `"[InternetExplorer]"`; tag name, `org.scalatest.tags.InternetExplorerBrowser`; and a factory method that produces a Selenium `InternetExplorerDriver`.
 *
 * This object's superclass, `BrowserInfo`, is used by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]: an `IndexedSeq[BrowserInfo]` is returned
 * from the `browsers` field of these traits to specify the browsers to share between tests.
 * When tests are registered, `AllBrowsersPerSuite` and `AllBrowsersPerTest` use the browser name to ensure the tests shared by multiple browsers
 * have unique names (the name of each shared test is appended with a browser name). When the tests run, these traits
 * use the `BrowserInfo`'s factory method to create `WebDriver`s as needed.
 * The `AllBrowsersPerSuite` and `AllBrowsersPerTest` traits use the  tag name to automatically tag any tests that use
 * a particular `WebDriver` with the appropriate tag so that tests can be dynamically filtered by the browser the use.
 */
case class InternetExplorerInfo() extends BrowserInfo("[InternetExplorer]", "org.scalatest.tags.InternetExplorerBrowser") {

  /**
   * Creates a new instance of a Selenium `InternetExplorerDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicates Internet Explorer was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `InternetExplorerDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] if Internet Explorer
   * was not available on the host platform.
   */
  def createWebDriver(): WebDriver = InternetExplorerFactory.createWebDriver()
}

/**
 * Chrome browser info, which encapsulates the browser name, `"[Chrome]"`; tag name, `org.scalatest.tags.ChromeBrowser`; and a factory method that produces a Selenium `ChromeDriver`.
 *
 * This object's superclass, `BrowserInfo`, is used by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]: an `IndexedSeq[BrowserInfo]` is returned
 * from the `browsers` field of these traits to specify the browsers to share between tests.
 * When tests are registered, `AllBrowsersPerSuite` and `AllBrowsersPerTest` use the browser name to ensure the tests shared by multiple browsers
 * have unique names (the name of each shared test is appended with a browser name). When the tests run, these traits
 * use the `BrowserInfo`'s factory method to create `WebDriver`s as needed.
 * The `AllBrowsersPerSuite` and `AllBrowsersPerTest` traits use the  tag name to automatically tag any tests that use
 * a particular `WebDriver` with the appropriate tag so that tests can be dynamically filtered by the browser the use.
 */
case class ChromeInfo() extends BrowserInfo("[Chrome]", "org.scalatest.tags.ChromeBrowser") {

  /**
   * Creates a new instance of a Selenium `ChromeDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicates Chrome was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `ChromeDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] if Chrome
   * was not available on the host platform.
   */
  def createWebDriver(): WebDriver = ChromeFactory.createWebDriver()
}

/**
 * JBrowserDriver info, which encapsulates the browser name, `"[JBrowserDriver]"`; tag name, `org.scalatest.tags.JBrowserDriverBrowser`; and a factory method that produces a Selenium `ChromeDriver`.
 *
 * This object's superclass, `BrowserInfo`, is used by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]: an `IndexedSeq[BrowserInfo]` is returned
 * from the `browsers` field of these traits to specify the browsers to share between tests.
 * When tests are registered, `AllBrowsersPerSuite` and `AllBrowsersPerTest` use the browser name to ensure the tests shared by multiple browsers
 * have unique names (the name of each shared test is appended with a browser name). When the tests run, these traits
 * use the `BrowserInfo`'s factory method to create `WebDriver`s as needed.
 * The `AllBrowsersPerSuite` and `AllBrowsersPerTest` traits use the  tag name to automatically tag any tests that use
 * a particular `WebDriver` with the appropriate tag so that tests can be dynamically filtered by the browser the use.
 */
case class JBrowserDriverInfo(settings: Settings = JBrowserDriverFactory.settings) extends BrowserInfo("[JBrowserDriver]", "org.scalatest.tags.JBrowserDriverBrowser") {

  /**
   * Creates a new instance of a Selenium `JBrowserDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicates JBrowserDriver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `JBrowserDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] if JBrowserDriver was not available on the host platform.
   */
  def createWebDriver(): WebDriver = JBrowserDriverFactory.createWebDriver()
}

/**
 * `HtmlUnit` browser info, which encapsulates the browser name, `"[HtmlUnit]"`; tag name, `org.scalatest.tags.HtmlUnitBrowser`; and a factory method that produces a Selenium `HtmlUnitDriver`.
 *
 * This object's superclass, `BrowserInfo`, is used by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]: an `IndexedSeq[BrowserInfo]` is returned
 * from the `browsers` field of these traits to specify the browsers to share between tests.
 * When tests are registered, `AllBrowsersPerSuite` and `AllBrowsersPerTest` use the browser name to ensure the tests shared by multiple browsers
 * have unique names (the name of each shared test is appended with a browser name). When the tests run, these traits
 * use the `BrowserInfo`'s factory method to create `WebDriver`s as needed.
 * The `AllBrowsersPerSuite` and `AllBrowsersPerTest` traits use the  tag name to automatically tag any tests that use
 * a particular `WebDriver` with the appropriate tag so that tests can be dynamically filtered by the browser the use.
 */
case class HtmlUnitInfo(enableJavascript: Boolean = true) extends BrowserInfo("[HtmlUnit]", "org.scalatest.tags.HtmlUnitBrowser") {

  /**
   * Creates a new instance of a Selenium `HtmlUnitDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicates `HtmlUnit` was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `HtmlUnitDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] if `HtmlUnit`
   * was not available on the host platform.
   */
  def createWebDriver(): WebDriver = HtmlUnitFactory.createWebDriver(enableJavascript)
}

