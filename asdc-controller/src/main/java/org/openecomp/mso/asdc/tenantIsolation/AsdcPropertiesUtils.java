/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
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
package org.openecomp.mso.asdc.tenantIsolation;

import org.openecomp.mso.logger.MessageEnum;
import org.openecomp.mso.logger.MsoLogger;
import org.openecomp.mso.properties.MsoJavaProperties;
import org.openecomp.mso.properties.MsoPropertiesFactory;

public class AsdcPropertiesUtils {

	public final static String MSO_ASDC_CLIENTS = "MSO_ASDC_CLIENTS";
	
    private static MsoPropertiesFactory msoPropertiesFactory = new MsoPropertiesFactory ();

    private static MsoLogger msoLogger = MsoLogger.getMsoLogger (MsoLogger.Catalog.ASDC);

    private static boolean noProperties = true;

    public synchronized static MsoJavaProperties loadMsoProperties () {
        MsoJavaProperties msoProperties;
        try {
            msoProperties = msoPropertiesFactory.getMsoJavaProperties (MSO_ASDC_CLIENTS);
        } catch (Exception e) {
            msoLogger.error (MessageEnum.ASDC_PROPERTIES_NOT_FOUND, MSO_ASDC_CLIENTS, "", "", MsoLogger.ErrorCode.DataError, "Exception when loading MSO ASDC Clients Properties", e);
            return null;
        }

        if (msoProperties != null && msoProperties.size () > 0) {
        	noProperties = false;
            msoLogger.info (MessageEnum.ASDC_PROPERTIES_LOAD_SUCCESS, "", "");
            return msoProperties;
        } else {
            msoLogger.error (MessageEnum.ASDC_PROPERTIES_NOT_FOUND, MSO_ASDC_CLIENTS, "", "", MsoLogger.ErrorCode.DataError, "No MSO ASDC Clients Properties found");
            return null;
        }
    }
    
    public synchronized static final boolean getNoPropertiesState() {
    	return noProperties;
    }
}