/*
 * This file was automatically generated by EvoSuite
 * Mon Nov 14 11:46:25 GMT 2016
 */

/*- 
 * ============LICENSE_START======================================================= 
 * OPENECOMP - MSO 
 * ================================================================================ 
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved. 
 * ================================================================================ 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * ============LICENSE_END========================================================= 
 */ 

package org.openecomp.mso.rest;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.shaded.org.mockito.Mockito.*;
import static org.evosuite.runtime.MockitoExtension.*;
import static org.evosuite.runtime.EvoAssertions.*;

import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.evosuite.runtime.PrivateAccess;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, useJEE = true) 
public class APIResponseESTest extends APIResponseESTestscaffolding {

  @Test(timeout = 4000)
  public void test00()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 0, "Uc");
      basicHttpResponse0.addHeader("Uc", "org.apache.http.entity.ContentType");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      assertEquals(0, aPIResponse0.getStatusCode());
  }

  @Test(timeout = 4000)
  public void test01()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 1471, "0fVXWr>");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      int int0 = aPIResponse0.getStatusCode();
      assertEquals(1471, int0);
  }

  @Test(timeout = 4000)
  public void test02()  throws Throwable  {
      ProtocolVersion protocolVersion0 = mock(ProtocolVersion.class, new ViolatedAssumptionAnswer());
      StatusLine statusLine0 = mock(StatusLine.class, new ViolatedAssumptionAnswer());
      doReturn(protocolVersion0).when(statusLine0).getProtocolVersion();
      doReturn("Gi|Heay:?O.-PvSJFp").when(statusLine0).getReasonPhrase();
      doReturn((-1730834464), (-1730834464)).when(statusLine0).getStatusCode();
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse(statusLine0);
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      int int0 = aPIResponse0.getStatusCode();
      assertEquals((-1730834464), int0);
  }

  @Test(timeout = 4000)
  public void test03()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 1471, "0fVXWr>");
      byte[] byteArray0 = new byte[3];
      ByteArrayEntity byteArrayEntity0 = new ByteArrayEntity(byteArray0);
      basicHttpResponse0.setEntity(byteArrayEntity0);
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      byte[] byteArray1 = aPIResponse0.getResponseBodyAsByteArray();
      assertFalse(byteArray1.equals((Object)byteArray0));
  }

  @Test(timeout = 4000)
  public void test04()  throws Throwable  {
      ProtocolVersion protocolVersion0 = new ProtocolVersion("", 548, 548);
      BasicStatusLine basicStatusLine0 = new BasicStatusLine(protocolVersion0, 1196, " len: ");
      EnglishReasonPhraseCatalog englishReasonPhraseCatalog0 = EnglishReasonPhraseCatalog.INSTANCE;
      Locale locale0 = Locale.ITALY;
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((StatusLine) basicStatusLine0, (ReasonPhraseCatalog) englishReasonPhraseCatalog0, locale0);
      StringEntity stringEntity0 = new StringEntity("");
      basicHttpResponse0.setEntity(stringEntity0);
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      byte[] byteArray0 = aPIResponse0.getResponseBodyAsByteArray();
      assertArrayEquals(new byte[] {}, byteArray0);
  }

  @Test(timeout = 4000)
  public void test05()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 0, "'");
      basicHttpResponse0.addHeader("'", "'");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      String string0 = aPIResponse0.getFirstHeader("'");
      assertEquals("'", string0);
  }

  @Test(timeout = 4000)
  public void test06()  throws Throwable  {
      APIResponse aPIResponse0 = null;
      try {
        aPIResponse0 = new APIResponse((HttpResponse) null);
        fail("Expecting exception: NullPointerException");
      
      } catch(NullPointerException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("org.openecomp.mso.rest.APIResponse", e);
      }
  }

  @Test(timeout = 4000)
  public void test07()  throws Throwable  {
      ProtocolVersion protocolVersion0 = new ProtocolVersion("=", 1, 2);
      BasicStatusLine basicStatusLine0 = new BasicStatusLine(protocolVersion0, 1, "=");
      EnglishReasonPhraseCatalog englishReasonPhraseCatalog0 = EnglishReasonPhraseCatalog.INSTANCE;
      Locale locale0 = Locale.UK;
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((StatusLine) basicStatusLine0, (ReasonPhraseCatalog) englishReasonPhraseCatalog0, locale0);
      basicHttpResponse0.setStatusLine(protocolVersion0, 1);
      APIResponse aPIResponse0 = null;
      try {
        aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // Unknown category for status code 1
         //
         verifyException("org.apache.http.util.Args", e);
      }
  }

  @Test(timeout = 4000)
  public void test08()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 0, "");
      basicHttpResponse0.addHeader("", "");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      String string0 = aPIResponse0.getFirstHeader(",n6_`^Oyzn6YprnX");
      assertNull(string0);
  }

  @Test(timeout = 4000)
  public void test09()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 0, "");
      basicHttpResponse0.addHeader("", "");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      String string0 = aPIResponse0.getFirstHeader("");
      assertEquals("", string0);
  }

  @Test(timeout = 4000)
  public void test10()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 0, "");
      basicHttpResponse0.addHeader("", "");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      HttpHeader[] httpHeaderArray0 = aPIResponse0.getAllHeaders();
      assertNotNull(httpHeaderArray0);
  }

  @Test(timeout = 4000)
  public void test11()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 1471, "0fVXWr>");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      HttpHeader[] httpHeaderArray0 = aPIResponse0.getAllHeaders();
      assertNotNull(httpHeaderArray0);
  }

  @Test(timeout = 4000)
  public void test12()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 0, "c");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      String string0 = aPIResponse0.getResponseBodyAsString();
      assertEquals("", string0);
  }

  @Test(timeout = 4000)
  public void test13()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 1471, "0fVXWr>");
      byte[] byteArray0 = new byte[3];
      ByteArrayEntity byteArrayEntity0 = new ByteArrayEntity(byteArray0);
      basicHttpResponse0.setEntity(byteArrayEntity0);
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      String string0 = aPIResponse0.getResponseBodyAsString();
      assertEquals("\u0000\u0000\u0000", string0);
  }

  @Test(timeout = 4000)
  public void test14()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 0, "c");
      basicHttpResponse0.addHeader("c", "c");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      aPIResponse0.getResponseBodyAsString();
      basicHttpResponse0.getStatusLine();
      aPIResponse0.getStatusCode();
      HttpHeader[] httpHeaderArray0 = new HttpHeader[2];
      HttpHeader httpHeader0 = mock(HttpHeader.class, new ViolatedAssumptionAnswer());
      doReturn((String) null).when(httpHeader0).getName();
      httpHeaderArray0[0] = httpHeader0;
      HttpHeader httpHeader1 = mock(HttpHeader.class, new ViolatedAssumptionAnswer());
      httpHeaderArray0[1] = httpHeader1;
      PrivateAccess.setVariable((Class<APIResponse>) APIResponse.class, aPIResponse0, "headers", (Object) httpHeaderArray0);
      // Undeclared exception!
      try { 
        aPIResponse0.getFirstHeader("");
        fail("Expecting exception: NullPointerException");
      
      } catch(NullPointerException e) {
      }
  }

  @Test(timeout = 4000)
  public void test15()  throws Throwable  {
      BasicHttpResponse basicHttpResponse0 = new BasicHttpResponse((ProtocolVersion) null, 1471, "0fVXWr>");
      APIResponse aPIResponse0 = new APIResponse((HttpResponse) basicHttpResponse0);
      byte[] byteArray0 = aPIResponse0.getResponseBodyAsByteArray();
      assertNull(byteArray0);
  }
}
