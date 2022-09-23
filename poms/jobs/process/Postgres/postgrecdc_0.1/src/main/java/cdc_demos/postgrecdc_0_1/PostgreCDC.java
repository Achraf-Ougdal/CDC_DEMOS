// ============================================================================
//
// Copyright (c) 2006-2015, Talend SA
//
// Ce code source a été automatiquement généré par_Talend Open Studio for Data Integration
// / Soumis à la Licence Apache, Version 2.0 (la "Licence") ;
// votre utilisation de ce fichier doit respecter les termes de la Licence.
// Vous pouvez obtenir une copie de la Licence sur
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Sauf lorsqu'explicitement prévu par la loi en vigueur ou accepté par écrit, le logiciel
// distribué sous la Licence est distribué "TEL QUEL",
// SANS GARANTIE OU CONDITION D'AUCUNE SORTE, expresse ou implicite.
// Consultez la Licence pour connaître la terminologie spécifique régissant les autorisations et
// les limites prévues par la Licence.

package cdc_demos.postgrecdc_0_1;

import routines.Numeric;
import routines.DataOperation;
import routines.TalendDataGenerator;
import routines.TalendStringUtil;
import routines.TalendString;
import routines.StringHandling;
import routines.Relational;
import routines.TalendDate;
import routines.Mathematical;
import routines.system.*;
import routines.system.api.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Comparator;

@SuppressWarnings("unused")

/**
 * Job: PostgreCDC Purpose: <br>
 * Description: <br>
 * 
 * @author user@talend.com
 * @version 8.0.1.20211109_1610
 * @status
 */
public class PostgreCDC implements TalendJob {

	protected static void logIgnoredError(String message, Throwable cause) {
		System.err.println(message);
		if (cause != null) {
			cause.printStackTrace();
		}

	}

	public final Object obj = new Object();

	// for transmiting parameters purpose
	private Object valueObject = null;

	public Object getValueObject() {
		return this.valueObject;
	}

	public void setValueObject(Object valueObject) {
		this.valueObject = valueObject;
	}

	private final static String defaultCharset = java.nio.charset.Charset.defaultCharset().name();

	private final static String utf8Charset = "UTF-8";

	// contains type for every context property
	public class PropertiesWithType extends java.util.Properties {
		private static final long serialVersionUID = 1L;
		private java.util.Map<String, String> propertyTypes = new java.util.HashMap<>();

		public PropertiesWithType(java.util.Properties properties) {
			super(properties);
		}

		public PropertiesWithType() {
			super();
		}

		public void setContextType(String key, String type) {
			propertyTypes.put(key, type);
		}

		public String getContextType(String key) {
			return propertyTypes.get(key);
		}
	}

	// create and load default properties
	private java.util.Properties defaultProps = new java.util.Properties();

	// create application properties with default
	public class ContextProperties extends PropertiesWithType {

		private static final long serialVersionUID = 1L;

		public ContextProperties(java.util.Properties properties) {
			super(properties);
		}

		public ContextProperties() {
			super();
		}

		public void synchronizeContext() {

		}

		// if the stored or passed value is "<TALEND_NULL>" string, it mean null
		public String getStringValue(String key) {
			String origin_value = this.getProperty(key);
			if (NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY.equals(origin_value)) {
				return null;
			}
			return origin_value;
		}

	}

	protected ContextProperties context = new ContextProperties(); // will be instanciated by MS.

	public ContextProperties getContext() {
		return this.context;
	}

	private final String jobVersion = "0.1";
	private final String jobName = "PostgreCDC";
	private final String projectName = "CDC_DEMOS";
	public Integer errorCode = null;
	private String currentComponent = "";

	private final java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
	private final static java.util.Map<String, Object> junitGlobalMap = new java.util.HashMap<String, Object>();

	private final java.util.Map<String, Long> start_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Long> end_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Boolean> ok_Hash = new java.util.HashMap<String, Boolean>();
	public final java.util.List<String[]> globalBuffer = new java.util.ArrayList<String[]>();

	private RunStat runStat = new RunStat();

	// OSGi DataSource
	private final static String KEY_DB_DATASOURCES = "KEY_DB_DATASOURCES";

	private final static String KEY_DB_DATASOURCES_RAW = "KEY_DB_DATASOURCES_RAW";

	public void setDataSources(java.util.Map<String, javax.sql.DataSource> dataSources) {
		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		for (java.util.Map.Entry<String, javax.sql.DataSource> dataSourceEntry : dataSources.entrySet()) {
			talendDataSources.put(dataSourceEntry.getKey(),
					new routines.system.TalendDataSource(dataSourceEntry.getValue()));
		}
		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	public void setDataSourceReferences(List serviceReferences) throws Exception {

		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		java.util.Map<String, javax.sql.DataSource> dataSources = new java.util.HashMap<String, javax.sql.DataSource>();

		for (java.util.Map.Entry<String, javax.sql.DataSource> entry : BundleUtils
				.getServices(serviceReferences, javax.sql.DataSource.class).entrySet()) {
			dataSources.put(entry.getKey(), entry.getValue());
			talendDataSources.put(entry.getKey(), new routines.system.TalendDataSource(entry.getValue()));
		}

		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	private final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	private final java.io.PrintStream errorMessagePS = new java.io.PrintStream(new java.io.BufferedOutputStream(baos));

	public String getExceptionStackTrace() {
		if ("failure".equals(this.getStatus())) {
			errorMessagePS.flush();
			return baos.toString();
		}
		return null;
	}

	private Exception exception;

	public Exception getException() {
		if ("failure".equals(this.getStatus())) {
			return this.exception;
		}
		return null;
	}

	private class TalendException extends Exception {

		private static final long serialVersionUID = 1L;

		private java.util.Map<String, Object> globalMap = null;
		private Exception e = null;
		private String currentComponent = null;
		private String virtualComponentName = null;

		public void setVirtualComponentName(String virtualComponentName) {
			this.virtualComponentName = virtualComponentName;
		}

		private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
			this.currentComponent = errorComponent;
			this.globalMap = globalMap;
			this.e = e;
		}

		public Exception getException() {
			return this.e;
		}

		public String getCurrentComponent() {
			return this.currentComponent;
		}

		public String getExceptionCauseMessage(Exception e) {
			Throwable cause = e;
			String message = null;
			int i = 10;
			while (null != cause && 0 < i--) {
				message = cause.getMessage();
				if (null == message) {
					cause = cause.getCause();
				} else {
					break;
				}
			}
			if (null == message) {
				message = e.getClass().getName();
			}
			return message;
		}

		@Override
		public void printStackTrace() {
			if (!(e instanceof TalendException || e instanceof TDieException)) {
				if (virtualComponentName != null && currentComponent.indexOf(virtualComponentName + "_") == 0) {
					globalMap.put(virtualComponentName + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				}
				globalMap.put(currentComponent + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				System.err.println("Exception in component " + currentComponent + " (" + jobName + ")");
			}
			if (!(e instanceof TDieException)) {
				if (e instanceof TalendException) {
					e.printStackTrace();
				} else {
					e.printStackTrace();
					e.printStackTrace(errorMessagePS);
					PostgreCDC.this.exception = e;
				}
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(PostgreCDC.this, new Object[] { e, currentComponent, globalMap });
							break;
						}
					}

					if (!(e instanceof TDieException)) {
					}
				} catch (Exception e) {
					this.e.printStackTrace();
				}
			}
		}
	}

	public void tDBInput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tUniqRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tExtractJSONFields_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tHashOutput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tHashInput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tHashInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tLogRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tHashInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tHashInput_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tHashInput_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tMap_1_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tHashInput_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tDBRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tHashInput_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tPostjob_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tPostjob_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tDBClose_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tDBClose_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tPrejob_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tPrejob_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tDBConnection_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tDBConnection_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tDBInput_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tHashInput_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tHashInput_2_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tPostjob_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tDBClose_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tPrejob_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tDBConnection_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public static class row2Struct implements routines.system.IPersistableRow<row2Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_PostgreCDC = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_PostgreCDC = new byte[0];
		protected static final int DEFAULT_HASHCODE = 1;
		protected static final int PRIME = 31;
		protected int hashCode = DEFAULT_HASHCODE;
		public boolean hashCodeDirty = true;

		public String loopKey;

		public long event_id;

		public long getEvent_id() {
			return this.event_id;
		}

		public String schema_name;

		public String getSchema_name() {
			return this.schema_name;
		}

		public String table_name;

		public String getTable_name() {
			return this.table_name;
		}

		public String action;

		public String getAction() {
			return this.action;
		}

		public String row_data;

		public String getRow_data() {
			return this.row_data;
		}

		public Integer ID;

		public Integer getID() {
			return this.ID;
		}

		public String Name;

		public String getName() {
			return this.Name;
		}

		public String Age;

		public String getAge() {
			return this.Age;
		}

		@Override
		public int hashCode() {
			if (this.hashCodeDirty) {
				final int prime = PRIME;
				int result = DEFAULT_HASHCODE;

				result = prime * result + (int) this.event_id;

				this.hashCode = result;
				this.hashCodeDirty = false;
			}
			return this.hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final row2Struct other = (row2Struct) obj;

			if (this.event_id != other.event_id)
				return false;

			return true;
		}

		public void copyDataTo(row2Struct other) {

			other.event_id = this.event_id;
			other.schema_name = this.schema_name;
			other.table_name = this.table_name;
			other.action = this.action;
			other.row_data = this.row_data;
			other.ID = this.ID;
			other.Name = this.Name;
			other.Age = this.Age;

		}

		public void copyKeysDataTo(row2Struct other) {

			other.event_id = this.event_id;

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.ID = readInteger(dis);

					this.Name = readString(dis);

					this.Age = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.ID = readInteger(dis);

					this.Name = readString(dis);

					this.Age = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// Integer

				writeInteger(this.ID, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Age, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// Integer

				writeInteger(this.ID, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Age, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("event_id=" + String.valueOf(event_id));
			sb.append(",schema_name=" + schema_name);
			sb.append(",table_name=" + table_name);
			sb.append(",action=" + action);
			sb.append(",row_data=" + row_data);
			sb.append(",ID=" + String.valueOf(ID));
			sb.append(",Name=" + Name);
			sb.append(",Age=" + Age);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row2Struct other) {

			int returnValue = -1;

			returnValue = checkNullsAndCompare(this.event_id, other.event_id);
			if (returnValue != 0) {
				return returnValue;
			}

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row3Struct implements routines.system.IPersistableRow<row3Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_PostgreCDC = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_PostgreCDC = new byte[0];
		protected static final int DEFAULT_HASHCODE = 1;
		protected static final int PRIME = 31;
		protected int hashCode = DEFAULT_HASHCODE;
		public boolean hashCodeDirty = true;

		public String loopKey;

		public long event_id;

		public long getEvent_id() {
			return this.event_id;
		}

		public String schema_name;

		public String getSchema_name() {
			return this.schema_name;
		}

		public String table_name;

		public String getTable_name() {
			return this.table_name;
		}

		public Object relid;

		public Object getRelid() {
			return this.relid;
		}

		public String session_user_name;

		public String getSession_user_name() {
			return this.session_user_name;
		}

		public java.util.Date action_tstamp_tx;

		public java.util.Date getAction_tstamp_tx() {
			return this.action_tstamp_tx;
		}

		public java.util.Date action_tstamp_stm;

		public java.util.Date getAction_tstamp_stm() {
			return this.action_tstamp_stm;
		}

		public java.util.Date action_tstamp_clk;

		public java.util.Date getAction_tstamp_clk() {
			return this.action_tstamp_clk;
		}

		public Long transaction_id;

		public Long getTransaction_id() {
			return this.transaction_id;
		}

		public String application_name;

		public String getApplication_name() {
			return this.application_name;
		}

		public Object client_addr;

		public Object getClient_addr() {
			return this.client_addr;
		}

		public Integer client_port;

		public Integer getClient_port() {
			return this.client_port;
		}

		public String client_query;

		public String getClient_query() {
			return this.client_query;
		}

		public String action;

		public String getAction() {
			return this.action;
		}

		public String row_data;

		public String getRow_data() {
			return this.row_data;
		}

		public String changed_fields;

		public String getChanged_fields() {
			return this.changed_fields;
		}

		public boolean statement_only;

		public boolean getStatement_only() {
			return this.statement_only;
		}

		@Override
		public int hashCode() {
			if (this.hashCodeDirty) {
				final int prime = PRIME;
				int result = DEFAULT_HASHCODE;

				result = prime * result + (int) this.event_id;

				this.hashCode = result;
				this.hashCodeDirty = false;
			}
			return this.hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final row3Struct other = (row3Struct) obj;

			if (this.event_id != other.event_id)
				return false;

			return true;
		}

		public void copyDataTo(row3Struct other) {

			other.event_id = this.event_id;
			other.schema_name = this.schema_name;
			other.table_name = this.table_name;
			other.relid = this.relid;
			other.session_user_name = this.session_user_name;
			other.action_tstamp_tx = this.action_tstamp_tx;
			other.action_tstamp_stm = this.action_tstamp_stm;
			other.action_tstamp_clk = this.action_tstamp_clk;
			other.transaction_id = this.transaction_id;
			other.application_name = this.application_name;
			other.client_addr = this.client_addr;
			other.client_port = this.client_port;
			other.client_query = this.client_query;
			other.action = this.action;
			other.row_data = this.row_data;
			other.changed_fields = this.changed_fields;
			other.statement_only = this.statement_only;

		}

		public void copyKeysDataTo(row3Struct other) {

			other.event_id = this.event_id;

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private java.util.Date readDate(ObjectInputStream dis) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(dis.readLong());
			}
			return dateReturn;
		}

		private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = unmarshaller.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(unmarshaller.readLong());
			}
			return dateReturn;
		}

		private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException {
			if (date1 == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeLong(date1.getTime());
			}
		}

		private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (date1 == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeLong(date1.getTime());
			}
		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.relid = (Object) dis.readObject();

					this.session_user_name = readString(dis);

					this.action_tstamp_tx = readDate(dis);

					this.action_tstamp_stm = readDate(dis);

					this.action_tstamp_clk = readDate(dis);

					length = dis.readByte();
					if (length == -1) {
						this.transaction_id = null;
					} else {
						this.transaction_id = dis.readLong();
					}

					this.application_name = readString(dis);

					this.client_addr = (Object) dis.readObject();

					this.client_port = readInteger(dis);

					this.client_query = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.changed_fields = readString(dis);

					this.statement_only = dis.readBoolean();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.relid = (Object) dis.readObject();

					this.session_user_name = readString(dis);

					this.action_tstamp_tx = readDate(dis);

					this.action_tstamp_stm = readDate(dis);

					this.action_tstamp_clk = readDate(dis);

					length = dis.readByte();
					if (length == -1) {
						this.transaction_id = null;
					} else {
						this.transaction_id = dis.readLong();
					}

					this.application_name = readString(dis);

					this.client_addr = (Object) dis.readObject();

					this.client_port = readInteger(dis);

					this.client_query = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.changed_fields = readString(dis);

					this.statement_only = dis.readBoolean();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// Object

				dos.writeObject(this.relid);

				// String

				writeString(this.session_user_name, dos);

				// java.util.Date

				writeDate(this.action_tstamp_tx, dos);

				// java.util.Date

				writeDate(this.action_tstamp_stm, dos);

				// java.util.Date

				writeDate(this.action_tstamp_clk, dos);

				// Long

				if (this.transaction_id == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeLong(this.transaction_id);
				}

				// String

				writeString(this.application_name, dos);

				// Object

				dos.writeObject(this.client_addr);

				// Integer

				writeInteger(this.client_port, dos);

				// String

				writeString(this.client_query, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// String

				writeString(this.changed_fields, dos);

				// boolean

				dos.writeBoolean(this.statement_only);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// Object

				dos.writeObject(this.relid);

				// String

				writeString(this.session_user_name, dos);

				// java.util.Date

				writeDate(this.action_tstamp_tx, dos);

				// java.util.Date

				writeDate(this.action_tstamp_stm, dos);

				// java.util.Date

				writeDate(this.action_tstamp_clk, dos);

				// Long

				if (this.transaction_id == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeLong(this.transaction_id);
				}

				// String

				writeString(this.application_name, dos);

				// Object

				dos.writeObject(this.client_addr);

				// Integer

				writeInteger(this.client_port, dos);

				// String

				writeString(this.client_query, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// String

				writeString(this.changed_fields, dos);

				// boolean

				dos.writeBoolean(this.statement_only);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("event_id=" + String.valueOf(event_id));
			sb.append(",schema_name=" + schema_name);
			sb.append(",table_name=" + table_name);
			sb.append(",relid=" + String.valueOf(relid));
			sb.append(",session_user_name=" + session_user_name);
			sb.append(",action_tstamp_tx=" + String.valueOf(action_tstamp_tx));
			sb.append(",action_tstamp_stm=" + String.valueOf(action_tstamp_stm));
			sb.append(",action_tstamp_clk=" + String.valueOf(action_tstamp_clk));
			sb.append(",transaction_id=" + String.valueOf(transaction_id));
			sb.append(",application_name=" + application_name);
			sb.append(",client_addr=" + String.valueOf(client_addr));
			sb.append(",client_port=" + String.valueOf(client_port));
			sb.append(",client_query=" + client_query);
			sb.append(",action=" + action);
			sb.append(",row_data=" + row_data);
			sb.append(",changed_fields=" + changed_fields);
			sb.append(",statement_only=" + String.valueOf(statement_only));
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row3Struct other) {

			int returnValue = -1;

			returnValue = checkNullsAndCompare(this.event_id, other.event_id);
			if (returnValue != 0) {
				return returnValue;
			}

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class modificationsStruct implements routines.system.IPersistableRow<modificationsStruct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_PostgreCDC = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_PostgreCDC = new byte[0];
		protected static final int DEFAULT_HASHCODE = 1;
		protected static final int PRIME = 31;
		protected int hashCode = DEFAULT_HASHCODE;
		public boolean hashCodeDirty = true;

		public String loopKey;

		public long event_id;

		public long getEvent_id() {
			return this.event_id;
		}

		public String schema_name;

		public String getSchema_name() {
			return this.schema_name;
		}

		public String table_name;

		public String getTable_name() {
			return this.table_name;
		}

		public Object relid;

		public Object getRelid() {
			return this.relid;
		}

		public String session_user_name;

		public String getSession_user_name() {
			return this.session_user_name;
		}

		public java.util.Date action_tstamp_tx;

		public java.util.Date getAction_tstamp_tx() {
			return this.action_tstamp_tx;
		}

		public java.util.Date action_tstamp_stm;

		public java.util.Date getAction_tstamp_stm() {
			return this.action_tstamp_stm;
		}

		public java.util.Date action_tstamp_clk;

		public java.util.Date getAction_tstamp_clk() {
			return this.action_tstamp_clk;
		}

		public Long transaction_id;

		public Long getTransaction_id() {
			return this.transaction_id;
		}

		public String application_name;

		public String getApplication_name() {
			return this.application_name;
		}

		public Object client_addr;

		public Object getClient_addr() {
			return this.client_addr;
		}

		public Integer client_port;

		public Integer getClient_port() {
			return this.client_port;
		}

		public String client_query;

		public String getClient_query() {
			return this.client_query;
		}

		public String action;

		public String getAction() {
			return this.action;
		}

		public String row_data;

		public String getRow_data() {
			return this.row_data;
		}

		public String changed_fields;

		public String getChanged_fields() {
			return this.changed_fields;
		}

		public boolean statement_only;

		public boolean getStatement_only() {
			return this.statement_only;
		}

		@Override
		public int hashCode() {
			if (this.hashCodeDirty) {
				final int prime = PRIME;
				int result = DEFAULT_HASHCODE;

				result = prime * result + (int) this.event_id;

				this.hashCode = result;
				this.hashCodeDirty = false;
			}
			return this.hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final modificationsStruct other = (modificationsStruct) obj;

			if (this.event_id != other.event_id)
				return false;

			return true;
		}

		public void copyDataTo(modificationsStruct other) {

			other.event_id = this.event_id;
			other.schema_name = this.schema_name;
			other.table_name = this.table_name;
			other.relid = this.relid;
			other.session_user_name = this.session_user_name;
			other.action_tstamp_tx = this.action_tstamp_tx;
			other.action_tstamp_stm = this.action_tstamp_stm;
			other.action_tstamp_clk = this.action_tstamp_clk;
			other.transaction_id = this.transaction_id;
			other.application_name = this.application_name;
			other.client_addr = this.client_addr;
			other.client_port = this.client_port;
			other.client_query = this.client_query;
			other.action = this.action;
			other.row_data = this.row_data;
			other.changed_fields = this.changed_fields;
			other.statement_only = this.statement_only;

		}

		public void copyKeysDataTo(modificationsStruct other) {

			other.event_id = this.event_id;

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private java.util.Date readDate(ObjectInputStream dis) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(dis.readLong());
			}
			return dateReturn;
		}

		private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = unmarshaller.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(unmarshaller.readLong());
			}
			return dateReturn;
		}

		private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException {
			if (date1 == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeLong(date1.getTime());
			}
		}

		private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (date1 == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeLong(date1.getTime());
			}
		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.relid = (Object) dis.readObject();

					this.session_user_name = readString(dis);

					this.action_tstamp_tx = readDate(dis);

					this.action_tstamp_stm = readDate(dis);

					this.action_tstamp_clk = readDate(dis);

					length = dis.readByte();
					if (length == -1) {
						this.transaction_id = null;
					} else {
						this.transaction_id = dis.readLong();
					}

					this.application_name = readString(dis);

					this.client_addr = (Object) dis.readObject();

					this.client_port = readInteger(dis);

					this.client_query = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.changed_fields = readString(dis);

					this.statement_only = dis.readBoolean();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.relid = (Object) dis.readObject();

					this.session_user_name = readString(dis);

					this.action_tstamp_tx = readDate(dis);

					this.action_tstamp_stm = readDate(dis);

					this.action_tstamp_clk = readDate(dis);

					length = dis.readByte();
					if (length == -1) {
						this.transaction_id = null;
					} else {
						this.transaction_id = dis.readLong();
					}

					this.application_name = readString(dis);

					this.client_addr = (Object) dis.readObject();

					this.client_port = readInteger(dis);

					this.client_query = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.changed_fields = readString(dis);

					this.statement_only = dis.readBoolean();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// Object

				dos.writeObject(this.relid);

				// String

				writeString(this.session_user_name, dos);

				// java.util.Date

				writeDate(this.action_tstamp_tx, dos);

				// java.util.Date

				writeDate(this.action_tstamp_stm, dos);

				// java.util.Date

				writeDate(this.action_tstamp_clk, dos);

				// Long

				if (this.transaction_id == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeLong(this.transaction_id);
				}

				// String

				writeString(this.application_name, dos);

				// Object

				dos.writeObject(this.client_addr);

				// Integer

				writeInteger(this.client_port, dos);

				// String

				writeString(this.client_query, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// String

				writeString(this.changed_fields, dos);

				// boolean

				dos.writeBoolean(this.statement_only);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// Object

				dos.writeObject(this.relid);

				// String

				writeString(this.session_user_name, dos);

				// java.util.Date

				writeDate(this.action_tstamp_tx, dos);

				// java.util.Date

				writeDate(this.action_tstamp_stm, dos);

				// java.util.Date

				writeDate(this.action_tstamp_clk, dos);

				// Long

				if (this.transaction_id == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeLong(this.transaction_id);
				}

				// String

				writeString(this.application_name, dos);

				// Object

				dos.writeObject(this.client_addr);

				// Integer

				writeInteger(this.client_port, dos);

				// String

				writeString(this.client_query, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// String

				writeString(this.changed_fields, dos);

				// boolean

				dos.writeBoolean(this.statement_only);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("event_id=" + String.valueOf(event_id));
			sb.append(",schema_name=" + schema_name);
			sb.append(",table_name=" + table_name);
			sb.append(",relid=" + String.valueOf(relid));
			sb.append(",session_user_name=" + session_user_name);
			sb.append(",action_tstamp_tx=" + String.valueOf(action_tstamp_tx));
			sb.append(",action_tstamp_stm=" + String.valueOf(action_tstamp_stm));
			sb.append(",action_tstamp_clk=" + String.valueOf(action_tstamp_clk));
			sb.append(",transaction_id=" + String.valueOf(transaction_id));
			sb.append(",application_name=" + application_name);
			sb.append(",client_addr=" + String.valueOf(client_addr));
			sb.append(",client_port=" + String.valueOf(client_port));
			sb.append(",client_query=" + client_query);
			sb.append(",action=" + action);
			sb.append(",row_data=" + row_data);
			sb.append(",changed_fields=" + changed_fields);
			sb.append(",statement_only=" + String.valueOf(statement_only));
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(modificationsStruct other) {

			int returnValue = -1;

			returnValue = checkNullsAndCompare(this.event_id, other.event_id);
			if (returnValue != 0) {
				return returnValue;
			}

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void tDBInput_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tDBInput_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				modificationsStruct modifications = new modificationsStruct();
				row3Struct row3 = new row3Struct();
				row2Struct row2 = new row2Struct();

				/**
				 * [tHashOutput_1 begin ] start
				 */

				ok_Hash.put("tHashOutput_1", false);
				start_Hash.put("tHashOutput_1", System.currentTimeMillis());

				currentComponent = "tHashOutput_1";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "row2");
				}

				int tos_count_tHashOutput_1 = 0;

				org.talend.designer.components.hashfile.common.MapHashFile mf_tHashOutput_1 = org.talend.designer.components.hashfile.common.MapHashFile
						.getMapHashFile();
				org.talend.designer.components.hashfile.memory.AdvancedMemoryHashFile<row2Struct> tHashFile_tHashOutput_1 = null;
				String hashKey_tHashOutput_1 = "tHashFile_PostgreCDC_" + pid + "_tHashOutput_1";
				synchronized (org.talend.designer.components.hashfile.common.MapHashFile.resourceLockMap
						.get(hashKey_tHashOutput_1)) {
					if (mf_tHashOutput_1.getResourceMap().get(hashKey_tHashOutput_1) == null) {
						mf_tHashOutput_1.getResourceMap().put(hashKey_tHashOutput_1,
								new org.talend.designer.components.hashfile.memory.AdvancedMemoryHashFile<row2Struct>(
										org.talend.designer.components.hashfile.common.MATCHING_MODE.KEEP_ALL));
						tHashFile_tHashOutput_1 = mf_tHashOutput_1.getResourceMap().get(hashKey_tHashOutput_1);
					} else {
						tHashFile_tHashOutput_1 = mf_tHashOutput_1.getResourceMap().get(hashKey_tHashOutput_1);
					}
				}
				int nb_line_tHashOutput_1 = 0;

				/**
				 * [tHashOutput_1 begin ] stop
				 */

				/**
				 * [tExtractJSONFields_1 begin ] start
				 */

				ok_Hash.put("tExtractJSONFields_1", false);
				start_Hash.put("tExtractJSONFields_1", System.currentTimeMillis());

				currentComponent = "tExtractJSONFields_1";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "row3");
				}

				int tos_count_tExtractJSONFields_1 = 0;

				int nb_line_tExtractJSONFields_1 = 0;
				String jsonStr_tExtractJSONFields_1 = "";

				class JsonPathCache_tExtractJSONFields_1 {
					final java.util.Map<String, com.jayway.jsonpath.JsonPath> jsonPathString2compiledJsonPath = new java.util.HashMap<String, com.jayway.jsonpath.JsonPath>();

					public com.jayway.jsonpath.JsonPath getCompiledJsonPath(String jsonPath) {
						if (jsonPathString2compiledJsonPath.containsKey(jsonPath)) {
							return jsonPathString2compiledJsonPath.get(jsonPath);
						} else {
							com.jayway.jsonpath.JsonPath compiledLoopPath = com.jayway.jsonpath.JsonPath
									.compile(jsonPath);
							jsonPathString2compiledJsonPath.put(jsonPath, compiledLoopPath);
							return compiledLoopPath;
						}
					}
				}

				JsonPathCache_tExtractJSONFields_1 jsonPathCache_tExtractJSONFields_1 = new JsonPathCache_tExtractJSONFields_1();

				/**
				 * [tExtractJSONFields_1 begin ] stop
				 */

				/**
				 * [tUniqRow_1 begin ] start
				 */

				ok_Hash.put("tUniqRow_1", false);
				start_Hash.put("tUniqRow_1", System.currentTimeMillis());

				currentComponent = "tUniqRow_1";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "modifications");
				}

				int tos_count_tUniqRow_1 = 0;

				class KeyStruct_tUniqRow_1 {

					private static final int DEFAULT_HASHCODE = 1;
					private static final int PRIME = 31;
					private int hashCode = DEFAULT_HASHCODE;
					public boolean hashCodeDirty = true;

					Long transaction_id;

					@Override
					public int hashCode() {
						if (this.hashCodeDirty) {
							final int prime = PRIME;
							int result = DEFAULT_HASHCODE;

							result = prime * result
									+ ((this.transaction_id == null) ? 0 : this.transaction_id.hashCode());

							this.hashCode = result;
							this.hashCodeDirty = false;
						}
						return this.hashCode;
					}

					@Override
					public boolean equals(Object obj) {
						if (this == obj)
							return true;
						if (obj == null)
							return false;
						if (getClass() != obj.getClass())
							return false;
						final KeyStruct_tUniqRow_1 other = (KeyStruct_tUniqRow_1) obj;

						if (this.transaction_id == null) {
							if (other.transaction_id != null)
								return false;

						} else if (!this.transaction_id.equals(other.transaction_id))

							return false;

						return true;
					}

				}

				int nb_uniques_tUniqRow_1 = 0;
				int nb_duplicates_tUniqRow_1 = 0;
				KeyStruct_tUniqRow_1 finder_tUniqRow_1 = new KeyStruct_tUniqRow_1();
				java.util.Set<KeyStruct_tUniqRow_1> keystUniqRow_1 = new java.util.HashSet<KeyStruct_tUniqRow_1>();

				/**
				 * [tUniqRow_1 begin ] stop
				 */

				/**
				 * [tDBInput_1 begin ] start
				 */

				ok_Hash.put("tDBInput_1", false);
				start_Hash.put("tDBInput_1", System.currentTimeMillis());

				currentComponent = "tDBInput_1";

				int tos_count_tDBInput_1 = 0;

				int nb_line_tDBInput_1 = 0;
				java.sql.Connection conn_tDBInput_1 = null;
				conn_tDBInput_1 = (java.sql.Connection) globalMap.get("conn_tDBConnection_1");

				java.sql.Statement stmt_tDBInput_1 = conn_tDBInput_1.createStatement();

				String dbquery_tDBInput_1 = "SELECT \n  \"logged_actions\".\"event_id\", \n  \"logged_actions\".\"schema_name\", \n  \"logged_actions\".\"table_name\","
						+ " \n  \"logged_actions\".\"relid\", \n  \"logged_actions\".\"session_user_name\", \n  \"logged_actions\".\"action_tstamp_tx"
						+ "\", \n  \"logged_actions\".\"action_tstamp_stm\", \n  \"logged_actions\".\"action_tstamp_clk\", \n  \"logged_actions\".\"tr"
						+ "ansaction_id\", \n  \"logged_actions\".\"application_name\", \n  \"logged_actions\".\"client_addr\", \n  \"logged_actions\""
						+ ".\"client_port\", \n  \"logged_actions\".\"client_query\", \n  \"logged_actions\".\"action\", \n  \"logged_actions\".\"row_"
						+ "data\", \n  \"logged_actions\".\"changed_fields\", \n  \"logged_actions\".\"statement_only\"\nFROM audit.\"logged_actions\""
						+ "";

				globalMap.put("tDBInput_1_QUERY", dbquery_tDBInput_1);
				java.sql.ResultSet rs_tDBInput_1 = null;

				try {
					rs_tDBInput_1 = stmt_tDBInput_1.executeQuery(dbquery_tDBInput_1);
					java.sql.ResultSetMetaData rsmd_tDBInput_1 = rs_tDBInput_1.getMetaData();
					int colQtyInRs_tDBInput_1 = rsmd_tDBInput_1.getColumnCount();

					String tmpContent_tDBInput_1 = null;

					while (rs_tDBInput_1.next()) {
						nb_line_tDBInput_1++;

						if (colQtyInRs_tDBInput_1 < 1) {
							modifications.event_id = 0;
						} else {

							modifications.event_id = rs_tDBInput_1.getLong(1);
							if (rs_tDBInput_1.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_tDBInput_1 < 2) {
							modifications.schema_name = null;
						} else {

							modifications.schema_name = routines.system.JDBCUtil.getString(rs_tDBInput_1, 2, false);
						}
						if (colQtyInRs_tDBInput_1 < 3) {
							modifications.table_name = null;
						} else {

							modifications.table_name = routines.system.JDBCUtil.getString(rs_tDBInput_1, 3, false);
						}
						if (colQtyInRs_tDBInput_1 < 4) {
							modifications.relid = null;
						} else {

							modifications.relid = rs_tDBInput_1.getObject(4);
							if (rs_tDBInput_1.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_tDBInput_1 < 5) {
							modifications.session_user_name = null;
						} else {

							modifications.session_user_name = routines.system.JDBCUtil.getString(rs_tDBInput_1, 5,
									false);
						}
						if (colQtyInRs_tDBInput_1 < 6) {
							modifications.action_tstamp_tx = null;
						} else {

							modifications.action_tstamp_tx = routines.system.JDBCUtil.getDate(rs_tDBInput_1, 6);
						}
						if (colQtyInRs_tDBInput_1 < 7) {
							modifications.action_tstamp_stm = null;
						} else {

							modifications.action_tstamp_stm = routines.system.JDBCUtil.getDate(rs_tDBInput_1, 7);
						}
						if (colQtyInRs_tDBInput_1 < 8) {
							modifications.action_tstamp_clk = null;
						} else {

							modifications.action_tstamp_clk = routines.system.JDBCUtil.getDate(rs_tDBInput_1, 8);
						}
						if (colQtyInRs_tDBInput_1 < 9) {
							modifications.transaction_id = null;
						} else {

							modifications.transaction_id = rs_tDBInput_1.getLong(9);
							if (rs_tDBInput_1.wasNull()) {
								modifications.transaction_id = null;
							}
						}
						if (colQtyInRs_tDBInput_1 < 10) {
							modifications.application_name = null;
						} else {

							modifications.application_name = routines.system.JDBCUtil.getString(rs_tDBInput_1, 10,
									false);
						}
						if (colQtyInRs_tDBInput_1 < 11) {
							modifications.client_addr = null;
						} else {

							modifications.client_addr = rs_tDBInput_1.getObject(11);
							if (rs_tDBInput_1.wasNull()) {
								modifications.client_addr = null;
							}
						}
						if (colQtyInRs_tDBInput_1 < 12) {
							modifications.client_port = null;
						} else {

							modifications.client_port = rs_tDBInput_1.getInt(12);
							if (rs_tDBInput_1.wasNull()) {
								modifications.client_port = null;
							}
						}
						if (colQtyInRs_tDBInput_1 < 13) {
							modifications.client_query = null;
						} else {

							modifications.client_query = routines.system.JDBCUtil.getString(rs_tDBInput_1, 13, false);
						}
						if (colQtyInRs_tDBInput_1 < 14) {
							modifications.action = null;
						} else {

							modifications.action = routines.system.JDBCUtil.getString(rs_tDBInput_1, 14, false);
						}
						if (colQtyInRs_tDBInput_1 < 15) {
							modifications.row_data = null;
						} else {

							modifications.row_data = routines.system.JDBCUtil.getString(rs_tDBInput_1, 15, false);
						}
						if (colQtyInRs_tDBInput_1 < 16) {
							modifications.changed_fields = null;
						} else {

							modifications.changed_fields = routines.system.JDBCUtil.getString(rs_tDBInput_1, 16, false);
						}
						if (colQtyInRs_tDBInput_1 < 17) {
							modifications.statement_only = false;
						} else {

							modifications.statement_only = rs_tDBInput_1.getBoolean(17);
							if (rs_tDBInput_1.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}

						/**
						 * [tDBInput_1 begin ] stop
						 */

						/**
						 * [tDBInput_1 main ] start
						 */

						currentComponent = "tDBInput_1";

						tos_count_tDBInput_1++;

						/**
						 * [tDBInput_1 main ] stop
						 */

						/**
						 * [tDBInput_1 process_data_begin ] start
						 */

						currentComponent = "tDBInput_1";

						/**
						 * [tDBInput_1 process_data_begin ] stop
						 */

						/**
						 * [tUniqRow_1 main ] start
						 */

						currentComponent = "tUniqRow_1";

						if (execStat) {
							runStat.updateStatOnConnection(iterateId, 1, 1

									, "modifications"

							);
						}

						row3 = null;
						finder_tUniqRow_1.transaction_id = modifications.transaction_id;
						finder_tUniqRow_1.hashCodeDirty = true;
						if (!keystUniqRow_1.contains(finder_tUniqRow_1)) {
							KeyStruct_tUniqRow_1 new_tUniqRow_1 = new KeyStruct_tUniqRow_1();

							new_tUniqRow_1.transaction_id = modifications.transaction_id;

							keystUniqRow_1.add(new_tUniqRow_1);
							if (row3 == null) {

								row3 = new row3Struct();
							}
							row3.event_id = modifications.event_id;
							row3.schema_name = modifications.schema_name;
							row3.table_name = modifications.table_name;
							row3.relid = modifications.relid;
							row3.session_user_name = modifications.session_user_name;
							row3.action_tstamp_tx = modifications.action_tstamp_tx;
							row3.action_tstamp_stm = modifications.action_tstamp_stm;
							row3.action_tstamp_clk = modifications.action_tstamp_clk;
							row3.transaction_id = modifications.transaction_id;
							row3.application_name = modifications.application_name;
							row3.client_addr = modifications.client_addr;
							row3.client_port = modifications.client_port;
							row3.client_query = modifications.client_query;
							row3.action = modifications.action;
							row3.row_data = modifications.row_data;
							row3.changed_fields = modifications.changed_fields;
							row3.statement_only = modifications.statement_only;
							nb_uniques_tUniqRow_1++;
						} else {
							nb_duplicates_tUniqRow_1++;
						}

						tos_count_tUniqRow_1++;

						/**
						 * [tUniqRow_1 main ] stop
						 */

						/**
						 * [tUniqRow_1 process_data_begin ] start
						 */

						currentComponent = "tUniqRow_1";

						/**
						 * [tUniqRow_1 process_data_begin ] stop
						 */
// Start of branch "row3"
						if (row3 != null) {

							/**
							 * [tExtractJSONFields_1 main ] start
							 */

							currentComponent = "tExtractJSONFields_1";

							if (execStat) {
								runStat.updateStatOnConnection(iterateId, 1, 1

										, "row3"

								);
							}

							if (row3.row_data != null) {// C_01
								jsonStr_tExtractJSONFields_1 = row3.row_data.toString();

								row2 = null;

								String loopPath_tExtractJSONFields_1 = "$";
								java.util.List<Object> resultset_tExtractJSONFields_1 = new java.util.ArrayList<Object>();

								boolean isStructError_tExtractJSONFields_1 = true;
								com.jayway.jsonpath.ReadContext document_tExtractJSONFields_1 = null;
								try {
									document_tExtractJSONFields_1 = com.jayway.jsonpath.JsonPath
											.parse(jsonStr_tExtractJSONFields_1);
									com.jayway.jsonpath.JsonPath compiledLoopPath_tExtractJSONFields_1 = jsonPathCache_tExtractJSONFields_1
											.getCompiledJsonPath(loopPath_tExtractJSONFields_1);
									Object result_tExtractJSONFields_1 = document_tExtractJSONFields_1.read(
											compiledLoopPath_tExtractJSONFields_1, net.minidev.json.JSONObject.class);
									if (result_tExtractJSONFields_1 instanceof net.minidev.json.JSONArray) {
										resultset_tExtractJSONFields_1 = (net.minidev.json.JSONArray) result_tExtractJSONFields_1;
									} else {
										resultset_tExtractJSONFields_1.add(result_tExtractJSONFields_1);
									}

									isStructError_tExtractJSONFields_1 = false;
								} catch (java.lang.Exception ex_tExtractJSONFields_1) {
									globalMap.put("tExtractJSONFields_1_ERROR_MESSAGE",
											ex_tExtractJSONFields_1.getMessage());
									System.err.println(ex_tExtractJSONFields_1.getMessage());
								}

								String jsonPath_tExtractJSONFields_1 = null;
								com.jayway.jsonpath.JsonPath compiledJsonPath_tExtractJSONFields_1 = null;

								Object value_tExtractJSONFields_1 = null;

								Object root_tExtractJSONFields_1 = null;
								for (int i_tExtractJSONFields_1 = 0; isStructError_tExtractJSONFields_1
										|| (i_tExtractJSONFields_1 < resultset_tExtractJSONFields_1
												.size()); i_tExtractJSONFields_1++) {
									if (!isStructError_tExtractJSONFields_1) {
										Object row_tExtractJSONFields_1 = resultset_tExtractJSONFields_1
												.get(i_tExtractJSONFields_1);
										row2 = null;
										row2 = new row2Struct();
										nb_line_tExtractJSONFields_1++;
										try {
											row2.event_id = row3.event_id;
											row2.schema_name = row3.schema_name;
											row2.table_name = row3.table_name;
											row2.action = row3.action;
											row2.row_data = row3.row_data;
											jsonPath_tExtractJSONFields_1 = "$.ID";
											compiledJsonPath_tExtractJSONFields_1 = jsonPathCache_tExtractJSONFields_1
													.getCompiledJsonPath(jsonPath_tExtractJSONFields_1);

											try {

												value_tExtractJSONFields_1 = compiledJsonPath_tExtractJSONFields_1
														.read(row_tExtractJSONFields_1);

												if (value_tExtractJSONFields_1 != null
														&& !value_tExtractJSONFields_1.toString().isEmpty()) {
													row2.ID = ParserUtils
															.parseTo_Integer(value_tExtractJSONFields_1.toString());
												} else {
													row2.ID =

															null

													;
												}
											} catch (com.jayway.jsonpath.PathNotFoundException e_tExtractJSONFields_1) {
												globalMap.put("tExtractJSONFields_1_ERROR_MESSAGE",
														e_tExtractJSONFields_1.getMessage());
												row2.ID =

														null

												;
											}
											jsonPath_tExtractJSONFields_1 = "$.Name[0]";
											compiledJsonPath_tExtractJSONFields_1 = jsonPathCache_tExtractJSONFields_1
													.getCompiledJsonPath(jsonPath_tExtractJSONFields_1);

											try {

												value_tExtractJSONFields_1 = compiledJsonPath_tExtractJSONFields_1
														.read(row_tExtractJSONFields_1);

												row2.Name = value_tExtractJSONFields_1 == null ?

														null

														: value_tExtractJSONFields_1.toString();
											} catch (com.jayway.jsonpath.PathNotFoundException e_tExtractJSONFields_1) {
												globalMap.put("tExtractJSONFields_1_ERROR_MESSAGE",
														e_tExtractJSONFields_1.getMessage());
												row2.Name =

														null

												;
											}
											jsonPath_tExtractJSONFields_1 = "$.Age[0]";
											compiledJsonPath_tExtractJSONFields_1 = jsonPathCache_tExtractJSONFields_1
													.getCompiledJsonPath(jsonPath_tExtractJSONFields_1);

											try {

												value_tExtractJSONFields_1 = compiledJsonPath_tExtractJSONFields_1
														.read(row_tExtractJSONFields_1);

												row2.Age = value_tExtractJSONFields_1 == null ?

														null

														: value_tExtractJSONFields_1.toString();
											} catch (com.jayway.jsonpath.PathNotFoundException e_tExtractJSONFields_1) {
												globalMap.put("tExtractJSONFields_1_ERROR_MESSAGE",
														e_tExtractJSONFields_1.getMessage());
												row2.Age =

														null

												;
											}
										} catch (java.lang.Exception ex_tExtractJSONFields_1) {
											globalMap.put("tExtractJSONFields_1_ERROR_MESSAGE",
													ex_tExtractJSONFields_1.getMessage());
											System.err.println(ex_tExtractJSONFields_1.getMessage());
											row2 = null;
										}

									}

									isStructError_tExtractJSONFields_1 = false;

//}

									tos_count_tExtractJSONFields_1++;

									/**
									 * [tExtractJSONFields_1 main ] stop
									 */

									/**
									 * [tExtractJSONFields_1 process_data_begin ] start
									 */

									currentComponent = "tExtractJSONFields_1";

									/**
									 * [tExtractJSONFields_1 process_data_begin ] stop
									 */
// Start of branch "row2"
									if (row2 != null) {

										/**
										 * [tHashOutput_1 main ] start
										 */

										currentComponent = "tHashOutput_1";

										if (execStat) {
											runStat.updateStatOnConnection(iterateId, 1, 1

													, "row2"

											);
										}

										row2Struct oneRow_tHashOutput_1 = new row2Struct();

										oneRow_tHashOutput_1.event_id = row2.event_id;
										oneRow_tHashOutput_1.schema_name = row2.schema_name;
										oneRow_tHashOutput_1.table_name = row2.table_name;
										oneRow_tHashOutput_1.action = row2.action;
										oneRow_tHashOutput_1.row_data = row2.row_data;
										oneRow_tHashOutput_1.ID = row2.ID;
										oneRow_tHashOutput_1.Name = row2.Name;
										oneRow_tHashOutput_1.Age = row2.Age;

										tHashFile_tHashOutput_1.put(oneRow_tHashOutput_1);
										nb_line_tHashOutput_1++;

										tos_count_tHashOutput_1++;

										/**
										 * [tHashOutput_1 main ] stop
										 */

										/**
										 * [tHashOutput_1 process_data_begin ] start
										 */

										currentComponent = "tHashOutput_1";

										/**
										 * [tHashOutput_1 process_data_begin ] stop
										 */

										/**
										 * [tHashOutput_1 process_data_end ] start
										 */

										currentComponent = "tHashOutput_1";

										/**
										 * [tHashOutput_1 process_data_end ] stop
										 */

									} // End of branch "row2"

									// end for
								}

							} // C_01

							/**
							 * [tExtractJSONFields_1 process_data_end ] start
							 */

							currentComponent = "tExtractJSONFields_1";

							/**
							 * [tExtractJSONFields_1 process_data_end ] stop
							 */

						} // End of branch "row3"

						/**
						 * [tUniqRow_1 process_data_end ] start
						 */

						currentComponent = "tUniqRow_1";

						/**
						 * [tUniqRow_1 process_data_end ] stop
						 */

						/**
						 * [tDBInput_1 process_data_end ] start
						 */

						currentComponent = "tDBInput_1";

						/**
						 * [tDBInput_1 process_data_end ] stop
						 */

						/**
						 * [tDBInput_1 end ] start
						 */

						currentComponent = "tDBInput_1";

					}
				} finally {
					if (rs_tDBInput_1 != null) {
						rs_tDBInput_1.close();
					}
					if (stmt_tDBInput_1 != null) {
						stmt_tDBInput_1.close();
					}
				}
				globalMap.put("tDBInput_1_NB_LINE", nb_line_tDBInput_1);

				ok_Hash.put("tDBInput_1", true);
				end_Hash.put("tDBInput_1", System.currentTimeMillis());

				/**
				 * [tDBInput_1 end ] stop
				 */

				/**
				 * [tUniqRow_1 end ] start
				 */

				currentComponent = "tUniqRow_1";

				globalMap.put("tUniqRow_1_NB_UNIQUES", nb_uniques_tUniqRow_1);
				globalMap.put("tUniqRow_1_NB_DUPLICATES", nb_duplicates_tUniqRow_1);

				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "modifications");
				}

				ok_Hash.put("tUniqRow_1", true);
				end_Hash.put("tUniqRow_1", System.currentTimeMillis());

				/**
				 * [tUniqRow_1 end ] stop
				 */

				/**
				 * [tExtractJSONFields_1 end ] start
				 */

				currentComponent = "tExtractJSONFields_1";

				globalMap.put("tExtractJSONFields_1_NB_LINE", nb_line_tExtractJSONFields_1);

				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "row3");
				}

				ok_Hash.put("tExtractJSONFields_1", true);
				end_Hash.put("tExtractJSONFields_1", System.currentTimeMillis());

				/**
				 * [tExtractJSONFields_1 end ] stop
				 */

				/**
				 * [tHashOutput_1 end ] start
				 */

				currentComponent = "tHashOutput_1";

				globalMap.put("tHashOutput_1_NB_LINE", nb_line_tHashOutput_1);
				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "row2");
				}

				ok_Hash.put("tHashOutput_1", true);
				end_Hash.put("tHashOutput_1", System.currentTimeMillis());

				/**
				 * [tHashOutput_1 end ] stop
				 */

			} // end the resume

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT", "CONNECTION:SUBJOB_OK:tDBInput_1:OnSubjobOk", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("OnSubjobOk1", 0, "ok");
			}

			tHashInput_1Process(globalMap);

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tDBInput_1 finally ] start
				 */

				currentComponent = "tDBInput_1";

				/**
				 * [tDBInput_1 finally ] stop
				 */

				/**
				 * [tUniqRow_1 finally ] start
				 */

				currentComponent = "tUniqRow_1";

				/**
				 * [tUniqRow_1 finally ] stop
				 */

				/**
				 * [tExtractJSONFields_1 finally ] start
				 */

				currentComponent = "tExtractJSONFields_1";

				/**
				 * [tExtractJSONFields_1 finally ] stop
				 */

				/**
				 * [tHashOutput_1 finally ] start
				 */

				currentComponent = "tHashOutput_1";

				/**
				 * [tHashOutput_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tDBInput_1_SUBPROCESS_STATE", 1);
	}

	public static class row1Struct implements routines.system.IPersistableRow<row1Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_PostgreCDC = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_PostgreCDC = new byte[0];
		protected static final int DEFAULT_HASHCODE = 1;
		protected static final int PRIME = 31;
		protected int hashCode = DEFAULT_HASHCODE;
		public boolean hashCodeDirty = true;

		public String loopKey;

		public long event_id;

		public long getEvent_id() {
			return this.event_id;
		}

		public String schema_name;

		public String getSchema_name() {
			return this.schema_name;
		}

		public String table_name;

		public String getTable_name() {
			return this.table_name;
		}

		public String action;

		public String getAction() {
			return this.action;
		}

		public String row_data;

		public String getRow_data() {
			return this.row_data;
		}

		public Integer ID;

		public Integer getID() {
			return this.ID;
		}

		public String Name;

		public String getName() {
			return this.Name;
		}

		public String Age;

		public String getAge() {
			return this.Age;
		}

		@Override
		public int hashCode() {
			if (this.hashCodeDirty) {
				final int prime = PRIME;
				int result = DEFAULT_HASHCODE;

				result = prime * result + (int) this.event_id;

				this.hashCode = result;
				this.hashCodeDirty = false;
			}
			return this.hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final row1Struct other = (row1Struct) obj;

			if (this.event_id != other.event_id)
				return false;

			return true;
		}

		public void copyDataTo(row1Struct other) {

			other.event_id = this.event_id;
			other.schema_name = this.schema_name;
			other.table_name = this.table_name;
			other.action = this.action;
			other.row_data = this.row_data;
			other.ID = this.ID;
			other.Name = this.Name;
			other.Age = this.Age;

		}

		public void copyKeysDataTo(row1Struct other) {

			other.event_id = this.event_id;

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.ID = readInteger(dis);

					this.Name = readString(dis);

					this.Age = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.ID = readInteger(dis);

					this.Name = readString(dis);

					this.Age = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// Integer

				writeInteger(this.ID, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Age, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// Integer

				writeInteger(this.ID, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Age, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("event_id=" + String.valueOf(event_id));
			sb.append(",schema_name=" + schema_name);
			sb.append(",table_name=" + table_name);
			sb.append(",action=" + action);
			sb.append(",row_data=" + row_data);
			sb.append(",ID=" + String.valueOf(ID));
			sb.append(",Name=" + Name);
			sb.append(",Age=" + Age);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row1Struct other) {

			int returnValue = -1;

			returnValue = checkNullsAndCompare(this.event_id, other.event_id);
			if (returnValue != 0) {
				return returnValue;
			}

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void tHashInput_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tHashInput_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				row1Struct row1 = new row1Struct();

				/**
				 * [tLogRow_1 begin ] start
				 */

				ok_Hash.put("tLogRow_1", false);
				start_Hash.put("tLogRow_1", System.currentTimeMillis());

				currentComponent = "tLogRow_1";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "row1");
				}

				int tos_count_tLogRow_1 = 0;

				///////////////////////

				class Util_tLogRow_1 {

					String[] des_top = { ".", ".", "-", "+" };

					String[] des_head = { "|=", "=|", "-", "+" };

					String[] des_bottom = { "'", "'", "-", "+" };

					String name = "";

					java.util.List<String[]> list = new java.util.ArrayList<String[]>();

					int[] colLengths = new int[8];

					public void addRow(String[] row) {

						for (int i = 0; i < 8; i++) {
							if (row[i] != null) {
								colLengths[i] = Math.max(colLengths[i], row[i].length());
							}
						}
						list.add(row);
					}

					public void setTableName(String name) {

						this.name = name;
					}

					public StringBuilder format() {

						StringBuilder sb = new StringBuilder();

						sb.append(print(des_top));

						int totals = 0;
						for (int i = 0; i < colLengths.length; i++) {
							totals = totals + colLengths[i];
						}

						// name
						sb.append("|");
						int k = 0;
						for (k = 0; k < (totals + 7 - name.length()) / 2; k++) {
							sb.append(' ');
						}
						sb.append(name);
						for (int i = 0; i < totals + 7 - name.length() - k; i++) {
							sb.append(' ');
						}
						sb.append("|\n");

						// head and rows
						sb.append(print(des_head));
						for (int i = 0; i < list.size(); i++) {

							String[] row = list.get(i);

							java.util.Formatter formatter = new java.util.Formatter(new StringBuilder());

							StringBuilder sbformat = new StringBuilder();
							sbformat.append("|%1$-");
							sbformat.append(colLengths[0]);
							sbformat.append("s");

							sbformat.append("|%2$-");
							sbformat.append(colLengths[1]);
							sbformat.append("s");

							sbformat.append("|%3$-");
							sbformat.append(colLengths[2]);
							sbformat.append("s");

							sbformat.append("|%4$-");
							sbformat.append(colLengths[3]);
							sbformat.append("s");

							sbformat.append("|%5$-");
							sbformat.append(colLengths[4]);
							sbformat.append("s");

							sbformat.append("|%6$-");
							sbformat.append(colLengths[5]);
							sbformat.append("s");

							sbformat.append("|%7$-");
							sbformat.append(colLengths[6]);
							sbformat.append("s");

							sbformat.append("|%8$-");
							sbformat.append(colLengths[7]);
							sbformat.append("s");

							sbformat.append("|\n");

							formatter.format(sbformat.toString(), (Object[]) row);

							sb.append(formatter.toString());
							if (i == 0)
								sb.append(print(des_head)); // print the head
						}

						// end
						sb.append(print(des_bottom));
						return sb;
					}

					private StringBuilder print(String[] fillChars) {
						StringBuilder sb = new StringBuilder();
						// first column
						sb.append(fillChars[0]);
						for (int i = 0; i < colLengths[0] - fillChars[0].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);

						for (int i = 0; i < colLengths[1] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[2] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[3] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[4] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[5] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[6] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);

						// last column
						for (int i = 0; i < colLengths[7] - fillChars[1].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[1]);
						sb.append("\n");
						return sb;
					}

					public boolean isTableEmpty() {
						if (list.size() > 1)
							return false;
						return true;
					}
				}
				Util_tLogRow_1 util_tLogRow_1 = new Util_tLogRow_1();
				util_tLogRow_1.setTableName("tLogRow_1");
				util_tLogRow_1.addRow(new String[] { "event_id", "schema_name", "table_name", "action", "row_data",
						"ID", "Name", "Age", });
				StringBuilder strBuffer_tLogRow_1 = null;
				int nb_line_tLogRow_1 = 0;
///////////////////////    			

				/**
				 * [tLogRow_1 begin ] stop
				 */

				/**
				 * [tHashInput_1 begin ] start
				 */

				ok_Hash.put("tHashInput_1", false);
				start_Hash.put("tHashInput_1", System.currentTimeMillis());

				currentComponent = "tHashInput_1";

				int tos_count_tHashInput_1 = 0;

				int nb_line_tHashInput_1 = 0;

				org.talend.designer.components.hashfile.common.MapHashFile mf_tHashInput_1 = org.talend.designer.components.hashfile.common.MapHashFile
						.getMapHashFile();
				org.talend.designer.components.hashfile.memory.AdvancedMemoryHashFile<row2Struct> tHashFile_tHashInput_1 = mf_tHashInput_1
						.getAdvancedMemoryHashFile("tHashFile_PostgreCDC_" + pid + "_tHashOutput_1");
				if (tHashFile_tHashInput_1 == null) {
					throw new RuntimeException(
							"The hash is not initialized : The hash must exist before you read from it");
				}
				java.util.Iterator<row2Struct> iterator_tHashInput_1 = tHashFile_tHashInput_1.iterator();
				while (iterator_tHashInput_1.hasNext()) {
					row2Struct next_tHashInput_1 = iterator_tHashInput_1.next();

					row1.event_id = next_tHashInput_1.event_id;
					row1.schema_name = next_tHashInput_1.schema_name;
					row1.table_name = next_tHashInput_1.table_name;
					row1.action = next_tHashInput_1.action;
					row1.row_data = next_tHashInput_1.row_data;
					row1.ID = next_tHashInput_1.ID;
					row1.Name = next_tHashInput_1.Name;
					row1.Age = next_tHashInput_1.Age;

					/**
					 * [tHashInput_1 begin ] stop
					 */

					/**
					 * [tHashInput_1 main ] start
					 */

					currentComponent = "tHashInput_1";

					tos_count_tHashInput_1++;

					/**
					 * [tHashInput_1 main ] stop
					 */

					/**
					 * [tHashInput_1 process_data_begin ] start
					 */

					currentComponent = "tHashInput_1";

					/**
					 * [tHashInput_1 process_data_begin ] stop
					 */

					/**
					 * [tLogRow_1 main ] start
					 */

					currentComponent = "tLogRow_1";

					if (execStat) {
						runStat.updateStatOnConnection(iterateId, 1, 1

								, "row1"

						);
					}

///////////////////////		

					String[] row_tLogRow_1 = new String[8];

					row_tLogRow_1[0] = String.valueOf(row1.event_id);

					if (row1.schema_name != null) { //
						row_tLogRow_1[1] = String.valueOf(row1.schema_name);

					} //

					if (row1.table_name != null) { //
						row_tLogRow_1[2] = String.valueOf(row1.table_name);

					} //

					if (row1.action != null) { //
						row_tLogRow_1[3] = String.valueOf(row1.action);

					} //

					if (row1.row_data != null) { //
						row_tLogRow_1[4] = String.valueOf(row1.row_data);

					} //

					if (row1.ID != null) { //
						row_tLogRow_1[5] = String.valueOf(row1.ID);

					} //

					if (row1.Name != null) { //
						row_tLogRow_1[6] = String.valueOf(row1.Name);

					} //

					if (row1.Age != null) { //
						row_tLogRow_1[7] = String.valueOf(row1.Age);

					} //

					util_tLogRow_1.addRow(row_tLogRow_1);
					nb_line_tLogRow_1++;
//////

//////                    

///////////////////////    			

					tos_count_tLogRow_1++;

					/**
					 * [tLogRow_1 main ] stop
					 */

					/**
					 * [tLogRow_1 process_data_begin ] start
					 */

					currentComponent = "tLogRow_1";

					/**
					 * [tLogRow_1 process_data_begin ] stop
					 */

					/**
					 * [tLogRow_1 process_data_end ] start
					 */

					currentComponent = "tLogRow_1";

					/**
					 * [tLogRow_1 process_data_end ] stop
					 */

					/**
					 * [tHashInput_1 process_data_end ] start
					 */

					currentComponent = "tHashInput_1";

					/**
					 * [tHashInput_1 process_data_end ] stop
					 */

					/**
					 * [tHashInput_1 end ] start
					 */

					currentComponent = "tHashInput_1";

					nb_line_tHashInput_1++;
				}

				org.talend.designer.components.hashfile.common.MapHashFile.resourceLockMap
						.remove("tHashFile_PostgreCDC_" + pid + "_tHashOutput_1");

				globalMap.put("tHashInput_1_NB_LINE", nb_line_tHashInput_1);

				ok_Hash.put("tHashInput_1", true);
				end_Hash.put("tHashInput_1", System.currentTimeMillis());

				/**
				 * [tHashInput_1 end ] stop
				 */

				/**
				 * [tLogRow_1 end ] start
				 */

				currentComponent = "tLogRow_1";

//////

				java.io.PrintStream consoleOut_tLogRow_1 = null;
				if (globalMap.get("tLogRow_CONSOLE") != null) {
					consoleOut_tLogRow_1 = (java.io.PrintStream) globalMap.get("tLogRow_CONSOLE");
				} else {
					consoleOut_tLogRow_1 = new java.io.PrintStream(new java.io.BufferedOutputStream(System.out));
					globalMap.put("tLogRow_CONSOLE", consoleOut_tLogRow_1);
				}

				consoleOut_tLogRow_1.println(util_tLogRow_1.format().toString());
				consoleOut_tLogRow_1.flush();
//////
				globalMap.put("tLogRow_1_NB_LINE", nb_line_tLogRow_1);

///////////////////////    			

				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "row1");
				}

				ok_Hash.put("tLogRow_1", true);
				end_Hash.put("tLogRow_1", System.currentTimeMillis());

				/**
				 * [tLogRow_1 end ] stop
				 */

			} // end the resume

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT", "CONNECTION:SUBJOB_OK:tHashInput_1:OnSubjobOk", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("OnSubjobOk2", 0, "ok");
			}

			tHashInput_2Process(globalMap);

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tHashInput_1 finally ] start
				 */

				currentComponent = "tHashInput_1";

				/**
				 * [tHashInput_1 finally ] stop
				 */

				/**
				 * [tLogRow_1 finally ] start
				 */

				currentComponent = "tLogRow_1";

				/**
				 * [tLogRow_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tHashInput_1_SUBPROCESS_STATE", 1);
	}

	public static class to_deleteStruct implements routines.system.IPersistableRow<to_deleteStruct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_PostgreCDC = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_PostgreCDC = new byte[0];
		protected static final int DEFAULT_HASHCODE = 1;
		protected static final int PRIME = 31;
		protected int hashCode = DEFAULT_HASHCODE;
		public boolean hashCodeDirty = true;

		public String loopKey;

		public long event_id;

		public long getEvent_id() {
			return this.event_id;
		}

		@Override
		public int hashCode() {
			if (this.hashCodeDirty) {
				final int prime = PRIME;
				int result = DEFAULT_HASHCODE;

				result = prime * result + (int) this.event_id;

				this.hashCode = result;
				this.hashCodeDirty = false;
			}
			return this.hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final to_deleteStruct other = (to_deleteStruct) obj;

			if (this.event_id != other.event_id)
				return false;

			return true;
		}

		public void copyDataTo(to_deleteStruct other) {

			other.event_id = this.event_id;

		}

		public void copyKeysDataTo(to_deleteStruct other) {

			other.event_id = this.event_id;

		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// long

				dos.writeLong(this.event_id);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// long

				dos.writeLong(this.event_id);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("event_id=" + String.valueOf(event_id));
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(to_deleteStruct other) {

			int returnValue = -1;

			returnValue = checkNullsAndCompare(this.event_id, other.event_id);
			if (returnValue != 0) {
				return returnValue;
			}

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row4Struct implements routines.system.IPersistableRow<row4Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_PostgreCDC = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_PostgreCDC = new byte[0];

		public long event_id;

		public long getEvent_id() {
			return this.event_id;
		}

		public String schema_name;

		public String getSchema_name() {
			return this.schema_name;
		}

		public String table_name;

		public String getTable_name() {
			return this.table_name;
		}

		public String action;

		public String getAction() {
			return this.action;
		}

		public String row_data;

		public String getRow_data() {
			return this.row_data;
		}

		public Integer ID;

		public Integer getID() {
			return this.ID;
		}

		public String Name;

		public String getName() {
			return this.Name;
		}

		public String Age;

		public String getAge() {
			return this.Age;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_PostgreCDC.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_PostgreCDC.length == 0) {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_PostgreCDC = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_PostgreCDC, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.ID = readInteger(dis);

					this.Name = readString(dis);

					this.Age = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_PostgreCDC) {

				try {

					int length = 0;

					this.event_id = dis.readLong();

					this.schema_name = readString(dis);

					this.table_name = readString(dis);

					this.action = readString(dis);

					this.row_data = readString(dis);

					this.ID = readInteger(dis);

					this.Name = readString(dis);

					this.Age = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// Integer

				writeInteger(this.ID, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Age, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// long

				dos.writeLong(this.event_id);

				// String

				writeString(this.schema_name, dos);

				// String

				writeString(this.table_name, dos);

				// String

				writeString(this.action, dos);

				// String

				writeString(this.row_data, dos);

				// Integer

				writeInteger(this.ID, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Age, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("event_id=" + String.valueOf(event_id));
			sb.append(",schema_name=" + schema_name);
			sb.append(",table_name=" + table_name);
			sb.append(",action=" + action);
			sb.append(",row_data=" + row_data);
			sb.append(",ID=" + String.valueOf(ID));
			sb.append(",Name=" + Name);
			sb.append(",Age=" + Age);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row4Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void tHashInput_2Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tHashInput_2_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				row4Struct row4 = new row4Struct();
				to_deleteStruct to_delete = new to_deleteStruct();

				/**
				 * [tDBRow_1 begin ] start
				 */

				ok_Hash.put("tDBRow_1", false);
				start_Hash.put("tDBRow_1", System.currentTimeMillis());

				currentComponent = "tDBRow_1";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "to_delete");
				}

				int tos_count_tDBRow_1 = 0;

				java.sql.Connection conn_tDBRow_1 = null;
				String query_tDBRow_1 = "";
				boolean whetherReject_tDBRow_1 = false;
				conn_tDBRow_1 = (java.sql.Connection) globalMap.get("conn_tDBConnection_1");

				resourceMap.put("conn_tDBRow_1", conn_tDBRow_1);
				java.sql.Statement stmt_tDBRow_1 = conn_tDBRow_1.createStatement();
				resourceMap.put("stmt_tDBRow_1", stmt_tDBRow_1);

				/**
				 * [tDBRow_1 begin ] stop
				 */

				/**
				 * [tMap_1 begin ] start
				 */

				ok_Hash.put("tMap_1", false);
				start_Hash.put("tMap_1", System.currentTimeMillis());

				currentComponent = "tMap_1";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "row4");
				}

				int tos_count_tMap_1 = 0;

// ###############################
// # Lookup's keys initialization
// ###############################        

// ###############################
// # Vars initialization
				class Var__tMap_1__Struct {
				}
				Var__tMap_1__Struct Var__tMap_1 = new Var__tMap_1__Struct();
// ###############################

// ###############################
// # Outputs initialization
				to_deleteStruct to_delete_tmp = new to_deleteStruct();
// ###############################

				/**
				 * [tMap_1 begin ] stop
				 */

				/**
				 * [tHashInput_2 begin ] start
				 */

				ok_Hash.put("tHashInput_2", false);
				start_Hash.put("tHashInput_2", System.currentTimeMillis());

				currentComponent = "tHashInput_2";

				int tos_count_tHashInput_2 = 0;

				int nb_line_tHashInput_2 = 0;

				org.talend.designer.components.hashfile.common.MapHashFile mf_tHashInput_2 = org.talend.designer.components.hashfile.common.MapHashFile
						.getMapHashFile();
				org.talend.designer.components.hashfile.memory.AdvancedMemoryHashFile<row2Struct> tHashFile_tHashInput_2 = mf_tHashInput_2
						.getAdvancedMemoryHashFile("tHashFile_PostgreCDC_" + pid + "_tHashOutput_1");
				if (tHashFile_tHashInput_2 == null) {
					throw new RuntimeException(
							"The hash is not initialized : The hash must exist before you read from it");
				}
				java.util.Iterator<row2Struct> iterator_tHashInput_2 = tHashFile_tHashInput_2.iterator();
				while (iterator_tHashInput_2.hasNext()) {
					row2Struct next_tHashInput_2 = iterator_tHashInput_2.next();

					row4.event_id = next_tHashInput_2.event_id;
					row4.schema_name = next_tHashInput_2.schema_name;
					row4.table_name = next_tHashInput_2.table_name;
					row4.action = next_tHashInput_2.action;
					row4.row_data = next_tHashInput_2.row_data;
					row4.ID = next_tHashInput_2.ID;
					row4.Name = next_tHashInput_2.Name;
					row4.Age = next_tHashInput_2.Age;

					/**
					 * [tHashInput_2 begin ] stop
					 */

					/**
					 * [tHashInput_2 main ] start
					 */

					currentComponent = "tHashInput_2";

					tos_count_tHashInput_2++;

					/**
					 * [tHashInput_2 main ] stop
					 */

					/**
					 * [tHashInput_2 process_data_begin ] start
					 */

					currentComponent = "tHashInput_2";

					/**
					 * [tHashInput_2 process_data_begin ] stop
					 */

					/**
					 * [tMap_1 main ] start
					 */

					currentComponent = "tMap_1";

					if (execStat) {
						runStat.updateStatOnConnection(iterateId, 1, 1

								, "row4"

						);
					}

					boolean hasCasePrimitiveKeyWithNull_tMap_1 = false;

					// ###############################
					// # Input tables (lookups)
					boolean rejectedInnerJoin_tMap_1 = false;
					boolean mainRowRejected_tMap_1 = false;

					// ###############################
					{ // start of Var scope

						// ###############################
						// # Vars tables

						Var__tMap_1__Struct Var = Var__tMap_1;// ###############################
						// ###############################
						// # Output tables

						to_delete = null;

// # Output table : 'to_delete'
						to_delete_tmp.event_id = row4.event_id;
						to_delete = to_delete_tmp;
// ###############################

					} // end of Var scope

					rejectedInnerJoin_tMap_1 = false;

					tos_count_tMap_1++;

					/**
					 * [tMap_1 main ] stop
					 */

					/**
					 * [tMap_1 process_data_begin ] start
					 */

					currentComponent = "tMap_1";

					/**
					 * [tMap_1 process_data_begin ] stop
					 */
// Start of branch "to_delete"
					if (to_delete != null) {

						/**
						 * [tDBRow_1 main ] start
						 */

						currentComponent = "tDBRow_1";

						if (execStat) {
							runStat.updateStatOnConnection(iterateId, 1, 1

									, "to_delete"

							);
						}

						query_tDBRow_1 = "DELETE FROM audit.\"logged_actions\" WHERE \"event_id\"="
								+ to_delete.event_id;
						whetherReject_tDBRow_1 = false;
						globalMap.put("tDBRow_1_QUERY", query_tDBRow_1);
						try {
							stmt_tDBRow_1.execute(query_tDBRow_1);

						} catch (java.lang.Exception e) {
							whetherReject_tDBRow_1 = true;

							System.err.print(e.getMessage());
							globalMap.put("tDBRow_1_ERROR_MESSAGE", e.getMessage());

						}

						tos_count_tDBRow_1++;

						/**
						 * [tDBRow_1 main ] stop
						 */

						/**
						 * [tDBRow_1 process_data_begin ] start
						 */

						currentComponent = "tDBRow_1";

						/**
						 * [tDBRow_1 process_data_begin ] stop
						 */

						/**
						 * [tDBRow_1 process_data_end ] start
						 */

						currentComponent = "tDBRow_1";

						/**
						 * [tDBRow_1 process_data_end ] stop
						 */

					} // End of branch "to_delete"

					/**
					 * [tMap_1 process_data_end ] start
					 */

					currentComponent = "tMap_1";

					/**
					 * [tMap_1 process_data_end ] stop
					 */

					/**
					 * [tHashInput_2 process_data_end ] start
					 */

					currentComponent = "tHashInput_2";

					/**
					 * [tHashInput_2 process_data_end ] stop
					 */

					/**
					 * [tHashInput_2 end ] start
					 */

					currentComponent = "tHashInput_2";

					nb_line_tHashInput_2++;
				}

				mf_tHashInput_2.clearCache("tHashFile_PostgreCDC_" + pid + "_tHashOutput_1");

				org.talend.designer.components.hashfile.common.MapHashFile.resourceLockMap
						.remove("tHashFile_PostgreCDC_" + pid + "_tHashOutput_1");

				globalMap.put("tHashInput_2_NB_LINE", nb_line_tHashInput_2);

				ok_Hash.put("tHashInput_2", true);
				end_Hash.put("tHashInput_2", System.currentTimeMillis());

				/**
				 * [tHashInput_2 end ] stop
				 */

				/**
				 * [tMap_1 end ] start
				 */

				currentComponent = "tMap_1";

// ###############################
// # Lookup hashes releasing
// ###############################      

				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "row4");
				}

				ok_Hash.put("tMap_1", true);
				end_Hash.put("tMap_1", System.currentTimeMillis());

				/**
				 * [tMap_1 end ] stop
				 */

				/**
				 * [tDBRow_1 end ] start
				 */

				currentComponent = "tDBRow_1";

				stmt_tDBRow_1.close();
				resourceMap.remove("stmt_tDBRow_1");
				resourceMap.put("statementClosed_tDBRow_1", true);
				resourceMap.put("finish_tDBRow_1", true);
				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "to_delete");
				}

				ok_Hash.put("tDBRow_1", true);
				end_Hash.put("tDBRow_1", System.currentTimeMillis());

				/**
				 * [tDBRow_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tHashInput_2 finally ] start
				 */

				currentComponent = "tHashInput_2";

				/**
				 * [tHashInput_2 finally ] stop
				 */

				/**
				 * [tMap_1 finally ] start
				 */

				currentComponent = "tMap_1";

				/**
				 * [tMap_1 finally ] stop
				 */

				/**
				 * [tDBRow_1 finally ] start
				 */

				currentComponent = "tDBRow_1";

				if (resourceMap.get("statementClosed_tDBRow_1") == null) {
					java.sql.Statement stmtToClose_tDBRow_1 = null;
					if ((stmtToClose_tDBRow_1 = (java.sql.Statement) resourceMap.remove("stmt_tDBRow_1")) != null) {
						stmtToClose_tDBRow_1.close();
					}
				}

				/**
				 * [tDBRow_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tHashInput_2_SUBPROCESS_STATE", 1);
	}

	public void tPostjob_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tPostjob_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [tPostjob_1 begin ] start
				 */

				ok_Hash.put("tPostjob_1", false);
				start_Hash.put("tPostjob_1", System.currentTimeMillis());

				currentComponent = "tPostjob_1";

				int tos_count_tPostjob_1 = 0;

				/**
				 * [tPostjob_1 begin ] stop
				 */

				/**
				 * [tPostjob_1 main ] start
				 */

				currentComponent = "tPostjob_1";

				tos_count_tPostjob_1++;

				/**
				 * [tPostjob_1 main ] stop
				 */

				/**
				 * [tPostjob_1 process_data_begin ] start
				 */

				currentComponent = "tPostjob_1";

				/**
				 * [tPostjob_1 process_data_begin ] stop
				 */

				/**
				 * [tPostjob_1 process_data_end ] start
				 */

				currentComponent = "tPostjob_1";

				/**
				 * [tPostjob_1 process_data_end ] stop
				 */

				/**
				 * [tPostjob_1 end ] start
				 */

				currentComponent = "tPostjob_1";

				ok_Hash.put("tPostjob_1", true);
				end_Hash.put("tPostjob_1", System.currentTimeMillis());

				if (execStat) {
					runStat.updateStatOnConnection("OnComponentOk2", 0, "ok");
				}
				tDBClose_1Process(globalMap);

				/**
				 * [tPostjob_1 end ] stop
				 */
			} // end the resume

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tPostjob_1 finally ] start
				 */

				currentComponent = "tPostjob_1";

				/**
				 * [tPostjob_1 finally ] stop
				 */
			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tPostjob_1_SUBPROCESS_STATE", 1);
	}

	public void tDBClose_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tDBClose_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [tDBClose_1 begin ] start
				 */

				ok_Hash.put("tDBClose_1", false);
				start_Hash.put("tDBClose_1", System.currentTimeMillis());

				currentComponent = "tDBClose_1";

				int tos_count_tDBClose_1 = 0;

				/**
				 * [tDBClose_1 begin ] stop
				 */

				/**
				 * [tDBClose_1 main ] start
				 */

				currentComponent = "tDBClose_1";

				java.sql.Connection conn_tDBClose_1 = (java.sql.Connection) globalMap.get("conn_tDBConnection_1");
				if (conn_tDBClose_1 != null && !conn_tDBClose_1.isClosed()) {
					conn_tDBClose_1.close();
				}

				tos_count_tDBClose_1++;

				/**
				 * [tDBClose_1 main ] stop
				 */

				/**
				 * [tDBClose_1 process_data_begin ] start
				 */

				currentComponent = "tDBClose_1";

				/**
				 * [tDBClose_1 process_data_begin ] stop
				 */

				/**
				 * [tDBClose_1 process_data_end ] start
				 */

				currentComponent = "tDBClose_1";

				/**
				 * [tDBClose_1 process_data_end ] stop
				 */

				/**
				 * [tDBClose_1 end ] start
				 */

				currentComponent = "tDBClose_1";

				ok_Hash.put("tDBClose_1", true);
				end_Hash.put("tDBClose_1", System.currentTimeMillis());

				/**
				 * [tDBClose_1 end ] stop
				 */
			} // end the resume

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tDBClose_1 finally ] start
				 */

				currentComponent = "tDBClose_1";

				/**
				 * [tDBClose_1 finally ] stop
				 */
			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tDBClose_1_SUBPROCESS_STATE", 1);
	}

	public void tPrejob_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tPrejob_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [tPrejob_1 begin ] start
				 */

				ok_Hash.put("tPrejob_1", false);
				start_Hash.put("tPrejob_1", System.currentTimeMillis());

				currentComponent = "tPrejob_1";

				int tos_count_tPrejob_1 = 0;

				/**
				 * [tPrejob_1 begin ] stop
				 */

				/**
				 * [tPrejob_1 main ] start
				 */

				currentComponent = "tPrejob_1";

				tos_count_tPrejob_1++;

				/**
				 * [tPrejob_1 main ] stop
				 */

				/**
				 * [tPrejob_1 process_data_begin ] start
				 */

				currentComponent = "tPrejob_1";

				/**
				 * [tPrejob_1 process_data_begin ] stop
				 */

				/**
				 * [tPrejob_1 process_data_end ] start
				 */

				currentComponent = "tPrejob_1";

				/**
				 * [tPrejob_1 process_data_end ] stop
				 */

				/**
				 * [tPrejob_1 end ] start
				 */

				currentComponent = "tPrejob_1";

				ok_Hash.put("tPrejob_1", true);
				end_Hash.put("tPrejob_1", System.currentTimeMillis());

				if (execStat) {
					runStat.updateStatOnConnection("OnComponentOk1", 0, "ok");
				}
				tDBConnection_1Process(globalMap);

				/**
				 * [tPrejob_1 end ] stop
				 */
			} // end the resume

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tPrejob_1 finally ] start
				 */

				currentComponent = "tPrejob_1";

				/**
				 * [tPrejob_1 finally ] stop
				 */
			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tPrejob_1_SUBPROCESS_STATE", 1);
	}

	public void tDBConnection_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tDBConnection_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [tDBConnection_1 begin ] start
				 */

				ok_Hash.put("tDBConnection_1", false);
				start_Hash.put("tDBConnection_1", System.currentTimeMillis());

				currentComponent = "tDBConnection_1";

				int tos_count_tDBConnection_1 = 0;

				String dbProperties_tDBConnection_1 = "";
				String url_tDBConnection_1 = "jdbc:postgresql://" + "localhost" + ":" + "5432" + "/" + "CDC_DEMO";

				if (dbProperties_tDBConnection_1 != null && !"".equals(dbProperties_tDBConnection_1.trim())) {
					url_tDBConnection_1 = url_tDBConnection_1 + "?" + dbProperties_tDBConnection_1;
				}
				String dbUser_tDBConnection_1 = "postgres";

				final String decryptedPassword_tDBConnection_1 = routines.system.PasswordEncryptUtil.decryptPassword(
						"enc:routine.encryption.key.v1:Zcp42+Z+uhOoFmc8xeGYuVfhLYDuDrvve4ucJ5vlWhoPJgQxJ1xTKPrBUA==");
				String dbPwd_tDBConnection_1 = decryptedPassword_tDBConnection_1;

				java.sql.Connection conn_tDBConnection_1 = null;

				java.util.Enumeration<java.sql.Driver> drivers_tDBConnection_1 = java.sql.DriverManager.getDrivers();
				java.util.Set<String> redShiftDriverNames_tDBConnection_1 = new java.util.HashSet<String>(
						java.util.Arrays.asList("com.amazon.redshift.jdbc.Driver", "com.amazon.redshift.jdbc41.Driver",
								"com.amazon.redshift.jdbc42.Driver"));
				while (drivers_tDBConnection_1.hasMoreElements()) {
					java.sql.Driver d_tDBConnection_1 = drivers_tDBConnection_1.nextElement();
					if (redShiftDriverNames_tDBConnection_1.contains(d_tDBConnection_1.getClass().getName())) {
						try {
							java.sql.DriverManager.deregisterDriver(d_tDBConnection_1);
							java.sql.DriverManager.registerDriver(d_tDBConnection_1);
						} catch (java.lang.Exception e_tDBConnection_1) {
							globalMap.put("tDBConnection_1_ERROR_MESSAGE", e_tDBConnection_1.getMessage());
							// do nothing
						}
					}
				}
				String driverClass_tDBConnection_1 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_tDBConnection_1 = java.lang.Class.forName(driverClass_tDBConnection_1);
				globalMap.put("driverClass_tDBConnection_1", driverClass_tDBConnection_1);

				conn_tDBConnection_1 = java.sql.DriverManager.getConnection(url_tDBConnection_1, dbUser_tDBConnection_1,
						dbPwd_tDBConnection_1);

				globalMap.put("conn_tDBConnection_1", conn_tDBConnection_1);
				if (null != conn_tDBConnection_1) {

					conn_tDBConnection_1.setAutoCommit(true);
				}

				globalMap.put("schema_" + "tDBConnection_1", "");

				/**
				 * [tDBConnection_1 begin ] stop
				 */

				/**
				 * [tDBConnection_1 main ] start
				 */

				currentComponent = "tDBConnection_1";

				tos_count_tDBConnection_1++;

				/**
				 * [tDBConnection_1 main ] stop
				 */

				/**
				 * [tDBConnection_1 process_data_begin ] start
				 */

				currentComponent = "tDBConnection_1";

				/**
				 * [tDBConnection_1 process_data_begin ] stop
				 */

				/**
				 * [tDBConnection_1 process_data_end ] start
				 */

				currentComponent = "tDBConnection_1";

				/**
				 * [tDBConnection_1 process_data_end ] stop
				 */

				/**
				 * [tDBConnection_1 end ] start
				 */

				currentComponent = "tDBConnection_1";

				ok_Hash.put("tDBConnection_1", true);
				end_Hash.put("tDBConnection_1", System.currentTimeMillis());

				/**
				 * [tDBConnection_1 end ] stop
				 */
			} // end the resume

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tDBConnection_1 finally ] start
				 */

				currentComponent = "tDBConnection_1";

				/**
				 * [tDBConnection_1 finally ] stop
				 */
			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tDBConnection_1_SUBPROCESS_STATE", 1);
	}

	public String resuming_logs_dir_path = null;
	public String resuming_checkpoint_path = null;
	public String parent_part_launcher = null;
	private String resumeEntryMethodName = null;
	private boolean globalResumeTicket = false;

	public boolean watch = false;
	// portStats is null, it means don't execute the statistics
	public Integer portStats = null;
	public int portTraces = 4334;
	public String clientHost;
	public String defaultClientHost = "localhost";
	public String contextStr = "Default";
	public boolean isDefaultContext = true;
	public String pid = "0";
	public String rootPid = null;
	public String fatherPid = null;
	public String fatherNode = null;
	public long startTime = 0;
	public boolean isChildJob = false;
	public String log4jLevel = "";

	private boolean enableLogStash;

	private boolean execStat = true;

	private ThreadLocal<java.util.Map<String, String>> threadLocal = new ThreadLocal<java.util.Map<String, String>>() {
		protected java.util.Map<String, String> initialValue() {
			java.util.Map<String, String> threadRunResultMap = new java.util.HashMap<String, String>();
			threadRunResultMap.put("errorCode", null);
			threadRunResultMap.put("status", "");
			return threadRunResultMap;
		};
	};

	protected PropertiesWithType context_param = new PropertiesWithType();
	public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();

	public String status = "";

	public static void main(String[] args) {
		final PostgreCDC PostgreCDCClass = new PostgreCDC();

		int exitCode = PostgreCDCClass.runJobInTOS(args);

		System.exit(exitCode);
	}

	public String[][] runJob(String[] args) {

		int exitCode = runJobInTOS(args);
		String[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };

		return bufferValue;
	}

	public boolean hastBufferOutputComponent() {
		boolean hastBufferOutput = false;

		return hastBufferOutput;
	}

	public int runJobInTOS(String[] args) {
		// reset status
		status = "";

		String lastStr = "";
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--context_param")) {
				lastStr = arg;
			} else if (lastStr.equals("")) {
				evalParam(arg);
			} else {
				evalParam(lastStr + " " + arg);
				lastStr = "";
			}
		}
		enableLogStash = "true".equalsIgnoreCase(System.getProperty("audit.enabled"));

		if (clientHost == null) {
			clientHost = defaultClientHost;
		}

		if (pid == null || "0".equals(pid)) {
			pid = TalendString.getAsciiRandomString(6);
		}

		if (rootPid == null) {
			rootPid = pid;
		}
		if (fatherPid == null) {
			fatherPid = pid;
		} else {
			isChildJob = true;
		}

		if (portStats != null) {
			// portStats = -1; //for testing
			if (portStats < 0 || portStats > 65535) {
				// issue:10869, the portStats is invalid, so this client socket can't open
				System.err.println("The statistics socket port " + portStats + " is invalid.");
				execStat = false;
			}
		} else {
			execStat = false;
		}
		boolean inOSGi = routines.system.BundleUtils.inOSGi();

		if (inOSGi) {
			java.util.Dictionary<String, Object> jobProperties = routines.system.BundleUtils.getJobProperties(jobName);

			if (jobProperties != null && jobProperties.get("context") != null) {
				contextStr = (String) jobProperties.get("context");
			}
		}

		try {
			// call job/subjob with an existing context, like: --context=production. if
			// without this parameter, there will use the default context instead.
			java.io.InputStream inContext = PostgreCDC.class.getClassLoader()
					.getResourceAsStream("cdc_demos/postgrecdc_0_1/contexts/" + contextStr + ".properties");
			if (inContext == null) {
				inContext = PostgreCDC.class.getClassLoader()
						.getResourceAsStream("config/contexts/" + contextStr + ".properties");
			}
			if (inContext != null) {
				try {
					// defaultProps is in order to keep the original context value
					if (context != null && context.isEmpty()) {
						defaultProps.load(inContext);
						context = new ContextProperties(defaultProps);
					}
				} finally {
					inContext.close();
				}
			} else if (!isDefaultContext) {
				// print info and job continue to run, for case: context_param is not empty.
				System.err.println("Could not find the context " + contextStr);
			}

			if (!context_param.isEmpty()) {
				context.putAll(context_param);
				// set types for params from parentJobs
				for (Object key : context_param.keySet()) {
					String context_key = key.toString();
					String context_type = context_param.getContextType(context_key);
					context.setContextType(context_key, context_type);

				}
			}
			class ContextProcessing {
				private void processContext_0() {
				}

				public void processAllContext() {
					processContext_0();
				}
			}

			new ContextProcessing().processAllContext();
		} catch (java.io.IOException ie) {
			System.err.println("Could not load context " + contextStr);
			ie.printStackTrace();
		}

		// get context value from parent directly
		if (parentContextMap != null && !parentContextMap.isEmpty()) {
		}

		// Resume: init the resumeUtil
		resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);
		resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
		resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);

		List<String> parametersToEncrypt = new java.util.ArrayList<String>();
		// Resume: jobStart
		resumeUtil.addLog("JOB_STARTED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "",
				"", "", "", "", resumeUtil.convertToJsonText(context, parametersToEncrypt));

		if (execStat) {
			try {
				runStat.openSocket(!isChildJob);
				runStat.setAllPID(rootPid, fatherPid, pid, jobName);
				runStat.startThreadStat(clientHost, portStats);
				runStat.updateStatOnJob(RunStat.JOBSTART, fatherNode);
			} catch (java.io.IOException ioException) {
				ioException.printStackTrace();
			}
		}

		java.util.concurrent.ConcurrentHashMap<Object, Object> concurrentHashMap = new java.util.concurrent.ConcurrentHashMap<Object, Object>();
		globalMap.put("concurrentHashMap", concurrentHashMap);

		long startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long endUsedMemory = 0;
		long end = 0;

		startTime = System.currentTimeMillis();

		this.globalResumeTicket = true;// to run tPreJob

		try {
			errorCode = null;
			tPrejob_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_tPrejob_1) {
			globalMap.put("tPrejob_1_SUBPROCESS_STATE", -1);

			e_tPrejob_1.printStackTrace();

		}

		this.globalResumeTicket = false;// to run others jobs

		try {
			errorCode = null;
			tDBInput_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_tDBInput_1) {
			globalMap.put("tDBInput_1_SUBPROCESS_STATE", -1);

			e_tDBInput_1.printStackTrace();

		}

		this.globalResumeTicket = true;// to run tPostJob

		try {
			errorCode = null;
			tPostjob_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_tPostjob_1) {
			globalMap.put("tPostjob_1_SUBPROCESS_STATE", -1);

			e_tPostjob_1.printStackTrace();

		}

		end = System.currentTimeMillis();

		if (watch) {
			System.out.println((end - startTime) + " milliseconds");
		}

		endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if (false) {
			System.out.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : PostgreCDC");
		}

		if (execStat) {
			runStat.updateStatOnJob(RunStat.JOBEND, fatherNode);
			runStat.stopThreadStat();
		}
		int returnCode = 0;

		if (errorCode == null) {
			returnCode = status != null && status.equals("failure") ? 1 : 0;
		} else {
			returnCode = errorCode.intValue();
		}
		resumeUtil.addLog("JOB_ENDED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "", "",
				"" + returnCode, "", "", "");

		return returnCode;

	}

	// only for OSGi env
	public void destroy() {
		closeSqlDbConnections();

	}

	private void closeSqlDbConnections() {
		try {
			Object obj_conn;
			obj_conn = globalMap.remove("conn_tDBConnection_1");
			if (null != obj_conn) {
				((java.sql.Connection) obj_conn).close();
			}
		} catch (java.lang.Exception e) {
		}
	}

	private java.util.Map<String, Object> getSharedConnections4REST() {
		java.util.Map<String, Object> connections = new java.util.HashMap<String, Object>();
		connections.put("conn_tDBConnection_1", globalMap.get("conn_tDBConnection_1"));

		return connections;
	}

	private void evalParam(String arg) {
		if (arg.startsWith("--resuming_logs_dir_path")) {
			resuming_logs_dir_path = arg.substring(25);
		} else if (arg.startsWith("--resuming_checkpoint_path")) {
			resuming_checkpoint_path = arg.substring(27);
		} else if (arg.startsWith("--parent_part_launcher")) {
			parent_part_launcher = arg.substring(23);
		} else if (arg.startsWith("--watch")) {
			watch = true;
		} else if (arg.startsWith("--stat_port=")) {
			String portStatsStr = arg.substring(12);
			if (portStatsStr != null && !portStatsStr.equals("null")) {
				portStats = Integer.parseInt(portStatsStr);
			}
		} else if (arg.startsWith("--trace_port=")) {
			portTraces = Integer.parseInt(arg.substring(13));
		} else if (arg.startsWith("--client_host=")) {
			clientHost = arg.substring(14);
		} else if (arg.startsWith("--context=")) {
			contextStr = arg.substring(10);
			isDefaultContext = false;
		} else if (arg.startsWith("--father_pid=")) {
			fatherPid = arg.substring(13);
		} else if (arg.startsWith("--root_pid=")) {
			rootPid = arg.substring(11);
		} else if (arg.startsWith("--father_node=")) {
			fatherNode = arg.substring(14);
		} else if (arg.startsWith("--pid=")) {
			pid = arg.substring(6);
		} else if (arg.startsWith("--context_type")) {
			String keyValue = arg.substring(15);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.setContextType(keyValue.substring(0, index),
							replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.setContextType(keyValue.substring(0, index), keyValue.substring(index + 1));
				}

			}

		} else if (arg.startsWith("--context_param")) {
			String keyValue = arg.substring(16);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));
				}
			}
		} else if (arg.startsWith("--log4jLevel=")) {
			log4jLevel = arg.substring(13);
		} else if (arg.startsWith("--audit.enabled") && arg.contains("=")) {// for trunjob call
			final int equal = arg.indexOf('=');
			final String key = arg.substring("--".length(), equal);
			System.setProperty(key, arg.substring(equal + 1));
		}
	}

	private static final String NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY = "<TALEND_NULL>";

	private final String[][] escapeChars = { { "\\\\", "\\" }, { "\\n", "\n" }, { "\\'", "\'" }, { "\\r", "\r" },
			{ "\\f", "\f" }, { "\\b", "\b" }, { "\\t", "\t" } };

	private String replaceEscapeChars(String keyValue) {

		if (keyValue == null || ("").equals(keyValue.trim())) {
			return keyValue;
		}

		StringBuilder result = new StringBuilder();
		int currIndex = 0;
		while (currIndex < keyValue.length()) {
			int index = -1;
			// judege if the left string includes escape chars
			for (String[] strArray : escapeChars) {
				index = keyValue.indexOf(strArray[0], currIndex);
				if (index >= 0) {

					result.append(keyValue.substring(currIndex, index + strArray[0].length()).replace(strArray[0],
							strArray[1]));
					currIndex = index + strArray[0].length();
					break;
				}
			}
			// if the left string doesn't include escape chars, append the left into the
			// result
			if (index < 0) {
				result.append(keyValue.substring(currIndex));
				currIndex = currIndex + keyValue.length();
			}
		}

		return result.toString();
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getStatus() {
		return status;
	}

	ResumeUtil resumeUtil = null;
}
/************************************************************************************************
 * 160334 characters generated by Talend Open Studio for Data Integration on the
 * 8 juillet 2022 à 17:44:16 WEST
 ************************************************************************************************/