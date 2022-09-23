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

package cdc_demos.fuzzymatching_0_1;

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
 * Job: FuzzyMatching Purpose: <br>
 * Description: <br>
 * 
 * @author user@talend.com
 * @version 8.0.1.20211109_1610
 * @status
 */
public class FuzzyMatching implements TalendJob {

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
	private final String jobName = "FuzzyMatching";
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

	LogCatcherUtils talendLogs_LOGS = new LogCatcherUtils();

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
					FuzzyMatching.this.exception = e;
				}
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(FuzzyMatching.this, new Object[] { e, currentComponent, globalMap });
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

	public void tFileInputDelimited_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		try {

			if (this.execStat) {
				runStat.updateStatOnConnection("OnComponentError1", 0, "error");
			}

			errorCode = null;
			tDie_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tLogRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tDie_1_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tDie_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tFileInputDelimited_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tAdvancedHash_row2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tMap_1_TMAP_OUT_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		tMap_1_TMAP_IN_error(exception, errorComponent, globalMap);

	}

	public void tMap_1_TMAP_IN_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void talendLogs_LOGS_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		talendLogs_CONSOLE_error(exception, errorComponent, globalMap);

	}

	public void talendLogs_CONSOLE_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		talendLogs_LOGS_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tFileInputDelimited_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tDie_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void talendLogs_LOGS_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public static class out1Struct implements routines.system.IPersistableRow<out1Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_FuzzyMatching = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[0];

		public String FirstName;

		public String getFirstName() {
			return this.FirstName;
		}

		public String VALUE;

		public String getVALUE() {
			return this.VALUE;
		}

		public String MATCHING;

		public String getMATCHING() {
			return this.MATCHING;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

					this.VALUE = readString(dis);

					this.MATCHING = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

					this.VALUE = readString(dis);

					this.MATCHING = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.FirstName, dos);

				// String

				writeString(this.VALUE, dos);

				// String

				writeString(this.MATCHING, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.FirstName, dos);

				// String

				writeString(this.VALUE, dos);

				// String

				writeString(this.MATCHING, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("FirstName=" + FirstName);
			sb.append(",VALUE=" + VALUE);
			sb.append(",MATCHING=" + MATCHING);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(out1Struct other) {

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

	public static class row1Struct implements routines.system.IPersistableRow<row1Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_FuzzyMatching = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[0];

		public String FirstName;

		public String getFirstName() {
			return this.FirstName;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("FirstName=" + FirstName);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row1Struct other) {

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

	public static class after_tFileInputDelimited_1Struct
			implements routines.system.IPersistableRow<after_tFileInputDelimited_1Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_FuzzyMatching = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[0];

		public String FirstName;

		public String getFirstName() {
			return this.FirstName;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("FirstName=" + FirstName);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(after_tFileInputDelimited_1Struct other) {

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

	public void tFileInputDelimited_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;
		String currentVirtualComponent = null;

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

				tFileInputDelimited_2Process(globalMap);

				row1Struct row1 = new row1Struct();
				out1Struct out1 = new out1Struct();

				/**
				 * [tMap_1_TMAP_OUT begin ] start
				 */

				ok_Hash.put("tMap_1_TMAP_OUT", false);
				start_Hash.put("tMap_1_TMAP_OUT", System.currentTimeMillis());

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_OUT";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "row1");
				}

				int tos_count_tMap_1_TMAP_OUT = 0;

// ###############################
// # Lookup's keys initialization

				org.talend.designer.components.lookup.persistent.PersistentSortedLookupManager<row2Struct> tHash_Lookup_row2 = (org.talend.designer.components.lookup.persistent.PersistentSortedLookupManager<row2Struct>) ((org.talend.designer.components.lookup.persistent.PersistentSortedLookupManager<row2Struct>) globalMap
						.get("tHash_Lookup_row2"));

				row2Struct row2HashKey = new row2Struct();
				row2Struct row2Default = new row2Struct();
// ###############################        

// ###############################
// # Vars initialization
				class Var__tMap_1_TMAP_OUT__Struct {
				}
				Var__tMap_1_TMAP_OUT__Struct Var__tMap_1_TMAP_OUT = new Var__tMap_1_TMAP_OUT__Struct();
// ###############################

// ###############################
// # Outputs initialization
// ###############################

				class SortableRow_tMap_1_1 implements Comparable<SortableRow_tMap_1_1>,
						routines.system.IPersistableRow<SortableRow_tMap_1_1> { // G_TM_B_001

					boolean is__rejectedInnerJoin;

					String exprKey_row2__FirstName;

					// row1
					String row1__FirstName;

					public void fillFrom(row1Struct row1, String exprKey_row2__FirstName) {

						this.row1__FirstName = row1.FirstName;

						this.exprKey_row2__FirstName = exprKey_row2__FirstName;

					}

					public void copyDataTo(row1Struct row1) {

						row1.FirstName = this.row1__FirstName;

					}

					public String toString() {

						StringBuilder sb = new StringBuilder();
						sb.append(super.toString());
						sb.append("[");

						sb.append("row1__FirstName");
						sb.append("=");
						sb.append(String.valueOf(this.row1__FirstName));

						sb.append("]");

						return sb.toString();
					}

					public int compareTo(SortableRow_tMap_1_1 other) {

						int returnValue = 0;

						returnValue = checkNullsAndCompare(this.exprKey_row2__FirstName, other.exprKey_row2__FirstName);
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

					public void readData(ObjectInputStream dis) {

						synchronized (row1Struct.commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

							try {

								int length = 0;

								this.is__rejectedInnerJoin = dis.readBoolean();

								length = dis.readInt();
								if (length == -1) {
									this.row1__FirstName = null;
								} else {
									if (length > row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
										if (length < 1024
												&& row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
										} else {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
										}
									}
									dis.readFully(row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
									this.row1__FirstName = new String(
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
								}

								length = dis.readInt();
								if (length == -1) {
									this.exprKey_row2__FirstName = null;
								} else {
									if (length > row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
										if (length < 1024
												&& row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
										} else {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
										}
									}
									dis.readFully(row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
									this.exprKey_row2__FirstName = new String(
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
								}

							} catch (IOException e) {
								throw new RuntimeException(e);

							}

						}
					}

					public void readData(org.jboss.marshalling.Unmarshaller objectIn) {

						synchronized (row1Struct.commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

							try {

								int length = 0;

								this.is__rejectedInnerJoin = objectIn.readBoolean();

								length = objectIn.readInt();
								if (length == -1) {
									this.row1__FirstName = null;
								} else {
									if (length > row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
										if (length < 1024
												&& row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
										} else {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
										}
									}
									objectIn.readFully(row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
									this.row1__FirstName = new String(
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
								}

								length = objectIn.readInt();
								if (length == -1) {
									this.exprKey_row2__FirstName = null;
								} else {
									if (length > row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
										if (length < 1024
												&& row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
										} else {
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
										}
									}
									objectIn.readFully(row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
									this.exprKey_row2__FirstName = new String(
											row1Struct.commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
								}

							} catch (IOException e) {
								throw new RuntimeException(e);

							}

						}
					}

					public void writeData(ObjectOutputStream dos) {
						try {

							dos.writeBoolean(this.is__rejectedInnerJoin);

							if (this.row1__FirstName == null) {
								dos.writeInt(-1);
							} else {
								byte[] byteArray = this.row1__FirstName.getBytes(utf8Charset);
								dos.writeInt(byteArray.length);
								dos.write(byteArray);
							}

							if (this.exprKey_row2__FirstName == null) {
								dos.writeInt(-1);
							} else {
								byte[] byteArray = this.exprKey_row2__FirstName.getBytes(utf8Charset);
								dos.writeInt(byteArray.length);
								dos.write(byteArray);
							}

						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}

					public void writeData(org.jboss.marshalling.Marshaller objectOut) {
						try {

							objectOut.writeBoolean(this.is__rejectedInnerJoin);

							if (this.row1__FirstName == null) {
								objectOut.writeInt(-1);
							} else {
								byte[] byteArray = this.row1__FirstName.getBytes(utf8Charset);
								objectOut.writeInt(byteArray.length);
								objectOut.write(byteArray);
							}

							if (this.exprKey_row2__FirstName == null) {
								objectOut.writeInt(-1);
							} else {
								byte[] byteArray = this.exprKey_row2__FirstName.getBytes(utf8Charset);
								objectOut.writeInt(byteArray.length);
								objectOut.write(byteArray);
							}

						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}

					public boolean supportJboss() {
						return true;
					}

				} // G_TM_B_001

				org.talend.designer.components.lookup.persistent.PersistentRowSorterIterator<SortableRow_tMap_1_1> fsi_tMap_1_1 = new org.talend.designer.components.lookup.persistent.PersistentRowSorterIterator<SortableRow_tMap_1_1>(
						"C:\\Users\\info\\Desktop\\Tmp" + "/" + jobName + "_tMapData_" + Thread.currentThread().getId()
								+ "_" + pid + "_tMap_1_1",
						2000000) {
					public SortableRow_tMap_1_1 createRowInstance() {
						return new SortableRow_tMap_1_1();
					}
				};

				fsi_tMap_1_1.initPut();

				/**
				 * [tMap_1_TMAP_OUT begin ] stop
				 */

				/**
				 * [tFileInputDelimited_1 begin ] start
				 */

				ok_Hash.put("tFileInputDelimited_1", false);
				start_Hash.put("tFileInputDelimited_1", System.currentTimeMillis());

				currentComponent = "tFileInputDelimited_1";

				int tos_count_tFileInputDelimited_1 = 0;

				final routines.system.RowState rowstate_tFileInputDelimited_1 = new routines.system.RowState();

				int nb_line_tFileInputDelimited_1 = 0;
				org.talend.fileprocess.FileInputDelimited fid_tFileInputDelimited_1 = null;
				int limit_tFileInputDelimited_1 = -1;
				try {

					Object filename_tFileInputDelimited_1 = "C:/Users/info/Desktop/testFuzzyMatching.txt";
					if (filename_tFileInputDelimited_1 instanceof java.io.InputStream) {

						int footer_value_tFileInputDelimited_1 = 0, random_value_tFileInputDelimited_1 = -1;
						if (footer_value_tFileInputDelimited_1 > 0 || random_value_tFileInputDelimited_1 > 0) {
							throw new java.lang.Exception(
									"When the input source is a stream,footer and random shouldn't be bigger than 0.");
						}

					}
					try {
						fid_tFileInputDelimited_1 = new org.talend.fileprocess.FileInputDelimited(
								"C:/Users/info/Desktop/testFuzzyMatching.txt", "US-ASCII", ";", "\n", false, 1, 0,
								limit_tFileInputDelimited_1, -1, false);
					} catch (java.lang.Exception e) {
						globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE", e.getMessage());

						throw e;

					}

					while (fid_tFileInputDelimited_1 != null && fid_tFileInputDelimited_1.nextRecord()) {
						rowstate_tFileInputDelimited_1.reset();

						row1 = null;

						boolean whetherReject_tFileInputDelimited_1 = false;
						row1 = new row1Struct();
						try {

							int columnIndexWithD_tFileInputDelimited_1 = 0;

							columnIndexWithD_tFileInputDelimited_1 = 0;

							row1.FirstName = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);

							if (rowstate_tFileInputDelimited_1.getException() != null) {
								throw rowstate_tFileInputDelimited_1.getException();
							}

						} catch (java.lang.Exception e) {
							globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE", e.getMessage());
							whetherReject_tFileInputDelimited_1 = true;

							throw (e);

						}

						/**
						 * [tFileInputDelimited_1 begin ] stop
						 */

						/**
						 * [tFileInputDelimited_1 main ] start
						 */

						currentComponent = "tFileInputDelimited_1";

						tos_count_tFileInputDelimited_1++;

						/**
						 * [tFileInputDelimited_1 main ] stop
						 */

						/**
						 * [tFileInputDelimited_1 process_data_begin ] start
						 */

						currentComponent = "tFileInputDelimited_1";

						/**
						 * [tFileInputDelimited_1 process_data_begin ] stop
						 */
// Start of branch "row1"
						if (row1 != null) {

							/**
							 * [tMap_1_TMAP_OUT main ] start
							 */

							currentVirtualComponent = "tMap_1";

							currentComponent = "tMap_1_TMAP_OUT";

							if (execStat) {
								runStat.updateStatOnConnection(iterateId, 1, 1

										, "row1"

								);
							}

							boolean hasCasePrimitiveKeyWithNull_tMap_1_TMAP_OUT = false;

							hasCasePrimitiveKeyWithNull_tMap_1_TMAP_OUT = false;

							String exprKey_row2__FirstName = row1.FirstName;

							SortableRow_tMap_1_1 sortableRow_tMap_1_1 = fsi_tMap_1_1.getNextFreeRow();

							sortableRow_tMap_1_1.fillFrom(row1, exprKey_row2__FirstName);

							fsi_tMap_1_1.put(sortableRow_tMap_1_1);

							// ###############################
							// # Input tables (lookups)
							boolean rejectedInnerJoin_tMap_1_TMAP_OUT = false;
							boolean mainRowRejected_tMap_1_TMAP_OUT = false;

							// ###############################

							tos_count_tMap_1_TMAP_OUT++;

							/**
							 * [tMap_1_TMAP_OUT main ] stop
							 */

							/**
							 * [tMap_1_TMAP_OUT process_data_begin ] start
							 */

							currentVirtualComponent = "tMap_1";

							currentComponent = "tMap_1_TMAP_OUT";

							/**
							 * [tMap_1_TMAP_OUT process_data_begin ] stop
							 */

							/**
							 * [tMap_1_TMAP_OUT process_data_end ] start
							 */

							currentVirtualComponent = "tMap_1";

							currentComponent = "tMap_1_TMAP_OUT";

							/**
							 * [tMap_1_TMAP_OUT process_data_end ] stop
							 */

						} // End of branch "row1"

						/**
						 * [tFileInputDelimited_1 process_data_end ] start
						 */

						currentComponent = "tFileInputDelimited_1";

						/**
						 * [tFileInputDelimited_1 process_data_end ] stop
						 */

						/**
						 * [tFileInputDelimited_1 end ] start
						 */

						currentComponent = "tFileInputDelimited_1";

					}
				} finally {
					if (!((Object) ("C:/Users/info/Desktop/testFuzzyMatching.txt") instanceof java.io.InputStream)) {
						if (fid_tFileInputDelimited_1 != null) {
							fid_tFileInputDelimited_1.close();
						}
					}
					if (fid_tFileInputDelimited_1 != null) {
						globalMap.put("tFileInputDelimited_1_NB_LINE", fid_tFileInputDelimited_1.getRowNumber());

					}
				}

				ok_Hash.put("tFileInputDelimited_1", true);
				end_Hash.put("tFileInputDelimited_1", System.currentTimeMillis());

				/**
				 * [tFileInputDelimited_1 end ] stop
				 */

				/**
				 * [tMap_1_TMAP_OUT end ] start
				 */

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_OUT";

// ###############################
// # Lookup hashes releasing
// ###############################      

				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "row1");
				}

				ok_Hash.put("tMap_1_TMAP_OUT", true);
				end_Hash.put("tMap_1_TMAP_OUT", System.currentTimeMillis());

				/**
				 * [tMap_1_TMAP_OUT end ] stop
				 */

				/**
				 * [tLogRow_1 begin ] start
				 */

				ok_Hash.put("tLogRow_1", false);
				start_Hash.put("tLogRow_1", System.currentTimeMillis());

				currentComponent = "tLogRow_1";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "out1");
				}

				int tos_count_tLogRow_1 = 0;

				///////////////////////

				class Util_tLogRow_1 {

					String[] des_top = { ".", ".", "-", "+" };

					String[] des_head = { "|=", "=|", "-", "+" };

					String[] des_bottom = { "'", "'", "-", "+" };

					String name = "";

					java.util.List<String[]> list = new java.util.ArrayList<String[]>();

					int[] colLengths = new int[3];

					public void addRow(String[] row) {

						for (int i = 0; i < 3; i++) {
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
						for (k = 0; k < (totals + 2 - name.length()) / 2; k++) {
							sb.append(' ');
						}
						sb.append(name);
						for (int i = 0; i < totals + 2 - name.length() - k; i++) {
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

						// last column
						for (int i = 0; i < colLengths[2] - fillChars[1].length() + 1; i++) {
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
				util_tLogRow_1.addRow(new String[] { "FirstName", "VALUE", "MATCHING", });
				StringBuilder strBuffer_tLogRow_1 = null;
				int nb_line_tLogRow_1 = 0;
///////////////////////    			

				/**
				 * [tLogRow_1 begin ] stop
				 */

				/**
				 * [tMap_1_TMAP_IN begin ] start
				 */

				ok_Hash.put("tMap_1_TMAP_IN", false);
				start_Hash.put("tMap_1_TMAP_IN", System.currentTimeMillis());

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_IN";

				int tos_count_tMap_1_TMAP_IN = 0;

// ###############################
// # Lookup's keys initialization
// ###############################        

// ###############################
// # Vars initialization
				class Var__tMap_1_TMAP_IN__Struct {
				}
				Var__tMap_1_TMAP_IN__Struct Var__tMap_1_TMAP_IN = new Var__tMap_1_TMAP_IN__Struct();
// ###############################

// ###############################
// # Outputs initialization
				out1Struct out1_tmp = new out1Struct();
// ###############################

				/**
				 * [tMap_1_TMAP_IN begin ] stop
				 */

				/**
				 * [tMap_1_TMAP_IN main ] start
				 */

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_IN";

				boolean hasCasePrimitiveKeyWithNull_tMap_1_TMAP_IN = false;

				fsi_tMap_1_1.endPut();

				if (row1 == null) {
					row1 = new row1Struct();
				}

				// ###############################
				// # Input tables (lookups)
				boolean rejectedInnerJoin_tMap_1_TMAP_IN = false;
				boolean mainRowRejected_tMap_1_TMAP_IN = false;

				///////////////////////////////////////////////
				// Starting Lookup Table "row2"
				///////////////////////////////////////////////

				SortableRow_tMap_1_1 rsc_tMap_1_1;

				tHash_Lookup_row2.initGet();

				fsi_tMap_1_1.initGet();

				// TEST firstLookupIsPersistentSorted=true isFirstPersistentSortedTable=true

				rejectedInnerJoin_tMap_1_TMAP_IN = false;

				while (fsi_tMap_1_1.hasNext()) { // G_TM_M_250 loop "1"

					// CALL close loop of lookup '1'

					rsc_tMap_1_1 = (SortableRow_tMap_1_1) fsi_tMap_1_1.next();
					rsc_tMap_1_1.copyDataTo(row1);

					rejectedInnerJoin_tMap_1_TMAP_IN = rsc_tMap_1_1.is__rejectedInnerJoin;

					boolean forceLooprow2 = false;

					row2Struct row2ObjectFromLookup = null;

					if (!rejectedInnerJoin_tMap_1_TMAP_IN) { // G_TM_M_020

						hasCasePrimitiveKeyWithNull_tMap_1_TMAP_IN = false;

						row2HashKey.FirstName = rsc_tMap_1_1.exprKey_row2__FirstName;

						tHash_Lookup_row2.lookup(row2HashKey);

						if (!tHash_Lookup_row2.hasNext()) { // G_TM_M_090

							forceLooprow2 = true;

						} // G_TM_M_090

					} // G_TM_M_020

					else { // G 20 - G 21
						forceLooprow2 = true;
					} // G 21

					row2Struct row2 = null;

					while ((tHash_Lookup_row2 != null && tHash_Lookup_row2.hasNext()) || forceLooprow2) { // G_TM_M_043

						// CALL close loop of lookup 'row2'

						row2Struct fromLookup_row2 = null;
						row2 = row2Default;

						if (!forceLooprow2) { // G 46

							fromLookup_row2 = tHash_Lookup_row2.next();

							if (fromLookup_row2 != null) {
								row2 = fromLookup_row2;
							}

						} // G 46

						forceLooprow2 = false;

						// ###############################
						{ // start of Var scope

							// ###############################
							// # Vars tables

							Var__tMap_1_TMAP_IN__Struct Var = Var__tMap_1_TMAP_IN;// ###############################
							// ###############################
							// # Output tables

							out1 = null;

// # Output table : 'out1'
							out1_tmp.FirstName = row1.FirstName;
							out1_tmp.VALUE = null;
							out1_tmp.MATCHING = row2.FirstName;
							out1 = out1_tmp;
// ###############################

						} // end of Var scope

						rejectedInnerJoin_tMap_1_TMAP_IN = false;

						tos_count_tMap_1_TMAP_IN++;

						/**
						 * [tMap_1_TMAP_IN main ] stop
						 */

						/**
						 * [tMap_1_TMAP_IN process_data_begin ] start
						 */

						currentVirtualComponent = "tMap_1";

						currentComponent = "tMap_1_TMAP_IN";

						/**
						 * [tMap_1_TMAP_IN process_data_begin ] stop
						 */
// Start of branch "out1"
						if (out1 != null) {

							/**
							 * [tLogRow_1 main ] start
							 */

							currentComponent = "tLogRow_1";

							if (execStat) {
								runStat.updateStatOnConnection(iterateId, 1, 1

										, "out1"

								);
							}

///////////////////////		

							String[] row_tLogRow_1 = new String[3];

							if (out1.FirstName != null) { //
								row_tLogRow_1[0] = String.valueOf(out1.FirstName);

							} //

							if (out1.VALUE != null) { //
								row_tLogRow_1[1] = String.valueOf(out1.VALUE);

							} //

							if (out1.MATCHING != null) { //
								row_tLogRow_1[2] = String.valueOf(out1.MATCHING);

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

						} // End of branch "out1"

					} // close loop of lookup 'row2' // G_TM_M_043

				} // G_TM_M_250 close loop read file data '1'

				/**
				 * [tMap_1_TMAP_IN process_data_end ] start
				 */

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_IN";

				/**
				 * [tMap_1_TMAP_IN process_data_end ] stop
				 */

				/**
				 * [tMap_1_TMAP_IN end ] start
				 */

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_IN";

// ###############################
// # Lookup hashes releasing
				fsi_tMap_1_1.endGet();

				if (tHash_Lookup_row2 != null) {
					tHash_Lookup_row2.endGet();
				}
				globalMap.remove("tHash_Lookup_row2");

// ###############################      

				ok_Hash.put("tMap_1_TMAP_IN", true);
				end_Hash.put("tMap_1_TMAP_IN", System.currentTimeMillis());

				/**
				 * [tMap_1_TMAP_IN end ] stop
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
					runStat.updateStat(resourceMap, iterateId, 2, 0, "out1");
				}

				ok_Hash.put("tLogRow_1", true);
				end_Hash.put("tLogRow_1", System.currentTimeMillis());

				/**
				 * [tLogRow_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			te.setVirtualComponentName(currentVirtualComponent);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tFileInputDelimited_1 finally ] start
				 */

				currentComponent = "tFileInputDelimited_1";

				/**
				 * [tFileInputDelimited_1 finally ] stop
				 */

				/**
				 * [tMap_1_TMAP_OUT finally ] start
				 */

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_OUT";

				/**
				 * [tMap_1_TMAP_OUT finally ] stop
				 */

				/**
				 * [tMap_1_TMAP_IN finally ] start
				 */

				currentVirtualComponent = "tMap_1";

				currentComponent = "tMap_1_TMAP_IN";

				/**
				 * [tMap_1_TMAP_IN finally ] stop
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

		globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", 1);
	}

	public void tDie_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tDie_1_SUBPROCESS_STATE", 0);

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
				 * [tDie_1 begin ] start
				 */

				ok_Hash.put("tDie_1", false);
				start_Hash.put("tDie_1", System.currentTimeMillis());

				currentComponent = "tDie_1";

				int tos_count_tDie_1 = 0;

				/**
				 * [tDie_1 begin ] stop
				 */

				/**
				 * [tDie_1 main ] start
				 */

				currentComponent = "tDie_1";

				try {
					globalMap.put("tDie_1_DIE_PRIORITY", 5);
					System.err.println("the end is near");

					globalMap.put("tDie_1_DIE_MESSAGE", "the end is near");
					globalMap.put("tDie_1_DIE_MESSAGES", "the end is near");

				} catch (Exception | Error e_tDie_1) {
					globalMap.put("tDie_1_ERROR_MESSAGE", e_tDie_1.getMessage());
					logIgnoredError(
							String.format("tDie_1 - tDie failed to log message due to internal error: %s", e_tDie_1),
							e_tDie_1);
				}

				currentComponent = "tDie_1";
				status = "failure";
				errorCode = new Integer(4);
				globalMap.put("tDie_1_DIE_CODE", errorCode);

				System.exit(4);

				tos_count_tDie_1++;

				/**
				 * [tDie_1 main ] stop
				 */

				/**
				 * [tDie_1 process_data_begin ] start
				 */

				currentComponent = "tDie_1";

				/**
				 * [tDie_1 process_data_begin ] stop
				 */

				/**
				 * [tDie_1 process_data_end ] start
				 */

				currentComponent = "tDie_1";

				/**
				 * [tDie_1 process_data_end ] stop
				 */

				/**
				 * [tDie_1 end ] start
				 */

				currentComponent = "tDie_1";

				ok_Hash.put("tDie_1", true);
				end_Hash.put("tDie_1", System.currentTimeMillis());

				/**
				 * [tDie_1 end ] stop
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
				 * [tDie_1 finally ] start
				 */

				currentComponent = "tDie_1";

				/**
				 * [tDie_1 finally ] stop
				 */
			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tDie_1_SUBPROCESS_STATE", 1);
	}

	public static class row2Struct implements routines.system.IPersistableComparableLookupRow<row2Struct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_FuzzyMatching = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[0];
		protected static final int DEFAULT_HASHCODE = 1;
		protected static final int PRIME = 31;
		protected int hashCode = DEFAULT_HASHCODE;
		public boolean hashCodeDirty = true;

		public String loopKey;

		public String FirstName;

		public String getFirstName() {
			return this.FirstName;
		}

		@Override
		public int hashCode() {
			if (this.hashCodeDirty) {
				final int prime = PRIME;
				int result = DEFAULT_HASHCODE;

				result = prime * result + ((this.FirstName == null) ? 0 : this.FirstName.hashCode());

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

			if (this.FirstName == null) {
				if (other.FirstName != null)
					return false;

			} else if (!this.FirstName.equals(other.FirstName))

				return false;

			return true;
		}

		public void copyDataTo(row2Struct other) {

			other.FirstName = this.FirstName;

		}

		public void copyKeysDataTo(row2Struct other) {

			other.FirstName = this.FirstName;

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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

		public void readKeysData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readKeysData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeKeysData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeKeysData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		/**
		 * Fill Values data by reading ObjectInputStream.
		 */
		public void readValuesData(DataInputStream dis, ObjectInputStream ois) {
			try {

				int length = 0;

			}

			finally {
			}

		}

		public void readValuesData(DataInputStream dis, org.jboss.marshalling.Unmarshaller objectIn) {
			try {
				int length = 0;

			}

			finally {
			}

		}

		/**
		 * Return a byte array which represents Values data.
		 */
		public void writeValuesData(DataOutputStream dos, ObjectOutputStream oos) {
			try {

			} finally {
			}

		}

		public void writeValuesData(DataOutputStream dos, org.jboss.marshalling.Marshaller objectOut) {
			try {

			} finally {
			}
		}

		public boolean supportMarshaller() {
			return true;
		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("FirstName=" + FirstName);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row2Struct other) {

			int returnValue = -1;

			returnValue = checkNullsAndCompare(this.FirstName, other.FirstName);
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

	public void tFileInputDelimited_2Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tFileInputDelimited_2_SUBPROCESS_STATE", 0);

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

				row2Struct row2 = new row2Struct();

				/**
				 * [tAdvancedHash_row2 begin ] start
				 */

				ok_Hash.put("tAdvancedHash_row2", false);
				start_Hash.put("tAdvancedHash_row2", System.currentTimeMillis());

				currentComponent = "tAdvancedHash_row2";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "row2");
				}

				int tos_count_tAdvancedHash_row2 = 0;

				// connection name:row2
				// source node:tFileInputDelimited_2 - inputs:(after_tFileInputDelimited_1)
				// outputs:(row2,row2) | target node:tAdvancedHash_row2 - inputs:(row2)
				// outputs:()
				// linked node: tMap_1 - inputs:(row1,row2) outputs:(out1)

				org.talend.designer.components.lookup.common.ICommonLookup.MATCHING_MODE matchingModeEnum_row2 = org.talend.designer.components.lookup.common.ICommonLookup.MATCHING_MODE.ALL_MATCHES;

				org.talend.designer.components.lookup.persistent.PersistentSortedLookupManager<row2Struct> tHash_Lookup_row2 = new org.talend.designer.components.lookup.persistent.PersistentSortedLookupManager<row2Struct>(
						matchingModeEnum_row2,
						"C:\\Users\\info\\Desktop\\Tmp" + "/" + jobName + "_tMapData_" + pid + "_Lookup_row2_",
						new org.talend.designer.components.persistent.IRowCreator() {
							public row2Struct createRowInstance() {
								return new row2Struct();
							}
						}

						, 2000000

				);

				tHash_Lookup_row2.initPut();

				globalMap.put("tHash_Lookup_row2", tHash_Lookup_row2);

				/**
				 * [tAdvancedHash_row2 begin ] stop
				 */

				/**
				 * [tFileInputDelimited_2 begin ] start
				 */

				ok_Hash.put("tFileInputDelimited_2", false);
				start_Hash.put("tFileInputDelimited_2", System.currentTimeMillis());

				currentComponent = "tFileInputDelimited_2";

				int tos_count_tFileInputDelimited_2 = 0;

				final routines.system.RowState rowstate_tFileInputDelimited_2 = new routines.system.RowState();

				int nb_line_tFileInputDelimited_2 = 0;
				org.talend.fileprocess.FileInputDelimited fid_tFileInputDelimited_2 = null;
				int limit_tFileInputDelimited_2 = -1;
				try {

					Object filename_tFileInputDelimited_2 = "C:/Users/info/Desktop/testFuzzyMatching.txt";
					if (filename_tFileInputDelimited_2 instanceof java.io.InputStream) {

						int footer_value_tFileInputDelimited_2 = 0, random_value_tFileInputDelimited_2 = -1;
						if (footer_value_tFileInputDelimited_2 > 0 || random_value_tFileInputDelimited_2 > 0) {
							throw new java.lang.Exception(
									"When the input source is a stream,footer and random shouldn't be bigger than 0.");
						}

					}
					try {
						fid_tFileInputDelimited_2 = new org.talend.fileprocess.FileInputDelimited(
								"C:/Users/info/Desktop/testFuzzyMatching.txt", "US-ASCII", ";", "\n", false, 1, 0,
								limit_tFileInputDelimited_2, -1, false);
					} catch (java.lang.Exception e) {
						globalMap.put("tFileInputDelimited_2_ERROR_MESSAGE", e.getMessage());

						System.err.println(e.getMessage());

					}

					while (fid_tFileInputDelimited_2 != null && fid_tFileInputDelimited_2.nextRecord()) {
						rowstate_tFileInputDelimited_2.reset();

						row2 = null;

						row2 = null;

						boolean whetherReject_tFileInputDelimited_2 = false;
						row2 = new row2Struct();
						try {

							int columnIndexWithD_tFileInputDelimited_2 = 0;

							columnIndexWithD_tFileInputDelimited_2 = 0;

							row2.FirstName = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

							if (rowstate_tFileInputDelimited_2.getException() != null) {
								throw rowstate_tFileInputDelimited_2.getException();
							}

						} catch (java.lang.Exception e) {
							globalMap.put("tFileInputDelimited_2_ERROR_MESSAGE", e.getMessage());
							whetherReject_tFileInputDelimited_2 = true;

							System.err.println(e.getMessage());
							row2 = null;

						}

						/**
						 * [tFileInputDelimited_2 begin ] stop
						 */

						/**
						 * [tFileInputDelimited_2 main ] start
						 */

						currentComponent = "tFileInputDelimited_2";

						tos_count_tFileInputDelimited_2++;

						/**
						 * [tFileInputDelimited_2 main ] stop
						 */

						/**
						 * [tFileInputDelimited_2 process_data_begin ] start
						 */

						currentComponent = "tFileInputDelimited_2";

						/**
						 * [tFileInputDelimited_2 process_data_begin ] stop
						 */
// Start of branch "row2"
						if (row2 != null) {

							/**
							 * [tAdvancedHash_row2 main ] start
							 */

							currentComponent = "tAdvancedHash_row2";

							if (execStat) {
								runStat.updateStatOnConnection(iterateId, 1, 1

										, "row2"

								);
							}

							row2Struct row2_HashRow = tHash_Lookup_row2.getNextFreeRow();

							row2_HashRow.FirstName = row2.FirstName;

							tHash_Lookup_row2.put(row2_HashRow);

							tos_count_tAdvancedHash_row2++;

							/**
							 * [tAdvancedHash_row2 main ] stop
							 */

							/**
							 * [tAdvancedHash_row2 process_data_begin ] start
							 */

							currentComponent = "tAdvancedHash_row2";

							/**
							 * [tAdvancedHash_row2 process_data_begin ] stop
							 */

							/**
							 * [tAdvancedHash_row2 process_data_end ] start
							 */

							currentComponent = "tAdvancedHash_row2";

							/**
							 * [tAdvancedHash_row2 process_data_end ] stop
							 */

						} // End of branch "row2"

						/**
						 * [tFileInputDelimited_2 process_data_end ] start
						 */

						currentComponent = "tFileInputDelimited_2";

						/**
						 * [tFileInputDelimited_2 process_data_end ] stop
						 */

						/**
						 * [tFileInputDelimited_2 end ] start
						 */

						currentComponent = "tFileInputDelimited_2";

					}
				} finally {
					if (!((Object) ("C:/Users/info/Desktop/testFuzzyMatching.txt") instanceof java.io.InputStream)) {
						if (fid_tFileInputDelimited_2 != null) {
							fid_tFileInputDelimited_2.close();
						}
					}
					if (fid_tFileInputDelimited_2 != null) {
						globalMap.put("tFileInputDelimited_2_NB_LINE", fid_tFileInputDelimited_2.getRowNumber());

					}
				}

				ok_Hash.put("tFileInputDelimited_2", true);
				end_Hash.put("tFileInputDelimited_2", System.currentTimeMillis());

				/**
				 * [tFileInputDelimited_2 end ] stop
				 */

				/**
				 * [tAdvancedHash_row2 end ] start
				 */

				currentComponent = "tAdvancedHash_row2";

				tHash_Lookup_row2.endPut();

				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "row2");
				}

				ok_Hash.put("tAdvancedHash_row2", true);
				end_Hash.put("tAdvancedHash_row2", System.currentTimeMillis());

				/**
				 * [tAdvancedHash_row2 end ] stop
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
				 * [tFileInputDelimited_2 finally ] start
				 */

				currentComponent = "tFileInputDelimited_2";

				/**
				 * [tFileInputDelimited_2 finally ] stop
				 */

				/**
				 * [tAdvancedHash_row2 finally ] start
				 */

				currentComponent = "tAdvancedHash_row2";

				/**
				 * [tAdvancedHash_row2 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tFileInputDelimited_2_SUBPROCESS_STATE", 1);
	}

	public static class row_talendLogs_LOGSStruct
			implements routines.system.IPersistableRow<row_talendLogs_LOGSStruct> {
		final static byte[] commonByteArrayLock_CDC_DEMOS_FuzzyMatching = new byte[0];
		static byte[] commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[0];

		public java.util.Date moment;

		public java.util.Date getMoment() {
			return this.moment;
		}

		public String pid;

		public String getPid() {
			return this.pid;
		}

		public String root_pid;

		public String getRoot_pid() {
			return this.root_pid;
		}

		public String father_pid;

		public String getFather_pid() {
			return this.father_pid;
		}

		public String project;

		public String getProject() {
			return this.project;
		}

		public String job;

		public String getJob() {
			return this.job;
		}

		public String context;

		public String getContext() {
			return this.context;
		}

		public Integer priority;

		public Integer getPriority() {
			return this.priority;
		}

		public String type;

		public String getType() {
			return this.type;
		}

		public String origin;

		public String getOrigin() {
			return this.origin;
		}

		public String message;

		public String getMessage() {
			return this.message;
		}

		public Integer code;

		public Integer getCode() {
			return this.code;
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

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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
				if (length > commonByteArray_CDC_DEMOS_FuzzyMatching.length) {
					if (length < 1024 && commonByteArray_CDC_DEMOS_FuzzyMatching.length == 0) {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[1024];
					} else {
						commonByteArray_CDC_DEMOS_FuzzyMatching = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length);
				strReturn = new String(commonByteArray_CDC_DEMOS_FuzzyMatching, 0, length, utf8Charset);
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

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.moment = readDate(dis);

					this.pid = readString(dis);

					this.root_pid = readString(dis);

					this.father_pid = readString(dis);

					this.project = readString(dis);

					this.job = readString(dis);

					this.context = readString(dis);

					this.priority = readInteger(dis);

					this.type = readString(dis);

					this.origin = readString(dis);

					this.message = readString(dis);

					this.code = readInteger(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_CDC_DEMOS_FuzzyMatching) {

				try {

					int length = 0;

					this.moment = readDate(dis);

					this.pid = readString(dis);

					this.root_pid = readString(dis);

					this.father_pid = readString(dis);

					this.project = readString(dis);

					this.job = readString(dis);

					this.context = readString(dis);

					this.priority = readInteger(dis);

					this.type = readString(dis);

					this.origin = readString(dis);

					this.message = readString(dis);

					this.code = readInteger(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// java.util.Date

				writeDate(this.moment, dos);

				// String

				writeString(this.pid, dos);

				// String

				writeString(this.root_pid, dos);

				// String

				writeString(this.father_pid, dos);

				// String

				writeString(this.project, dos);

				// String

				writeString(this.job, dos);

				// String

				writeString(this.context, dos);

				// Integer

				writeInteger(this.priority, dos);

				// String

				writeString(this.type, dos);

				// String

				writeString(this.origin, dos);

				// String

				writeString(this.message, dos);

				// Integer

				writeInteger(this.code, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// java.util.Date

				writeDate(this.moment, dos);

				// String

				writeString(this.pid, dos);

				// String

				writeString(this.root_pid, dos);

				// String

				writeString(this.father_pid, dos);

				// String

				writeString(this.project, dos);

				// String

				writeString(this.job, dos);

				// String

				writeString(this.context, dos);

				// Integer

				writeInteger(this.priority, dos);

				// String

				writeString(this.type, dos);

				// String

				writeString(this.origin, dos);

				// String

				writeString(this.message, dos);

				// Integer

				writeInteger(this.code, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("moment=" + String.valueOf(moment));
			sb.append(",pid=" + pid);
			sb.append(",root_pid=" + root_pid);
			sb.append(",father_pid=" + father_pid);
			sb.append(",project=" + project);
			sb.append(",job=" + job);
			sb.append(",context=" + context);
			sb.append(",priority=" + String.valueOf(priority));
			sb.append(",type=" + type);
			sb.append(",origin=" + origin);
			sb.append(",message=" + message);
			sb.append(",code=" + String.valueOf(code));
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row_talendLogs_LOGSStruct other) {

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

	public void talendLogs_LOGSProcess(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("talendLogs_LOGS_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;
		String currentVirtualComponent = null;

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

				row_talendLogs_LOGSStruct row_talendLogs_LOGS = new row_talendLogs_LOGSStruct();

				/**
				 * [talendLogs_CONSOLE begin ] start
				 */

				ok_Hash.put("talendLogs_CONSOLE", false);
				start_Hash.put("talendLogs_CONSOLE", System.currentTimeMillis());

				currentVirtualComponent = "talendLogs_CONSOLE";

				currentComponent = "talendLogs_CONSOLE";

				if (execStat) {
					runStat.updateStatOnConnection(resourceMap, iterateId, 0, 0, "Main");
				}

				int tos_count_talendLogs_CONSOLE = 0;

				///////////////////////

				final String OUTPUT_FIELD_SEPARATOR_talendLogs_CONSOLE = "|";
				java.io.PrintStream consoleOut_talendLogs_CONSOLE = null;

				StringBuilder strBuffer_talendLogs_CONSOLE = null;
				int nb_line_talendLogs_CONSOLE = 0;
///////////////////////    			

				/**
				 * [talendLogs_CONSOLE begin ] stop
				 */

				/**
				 * [talendLogs_LOGS begin ] start
				 */

				ok_Hash.put("talendLogs_LOGS", false);
				start_Hash.put("talendLogs_LOGS", System.currentTimeMillis());

				currentVirtualComponent = "talendLogs_LOGS";

				currentComponent = "talendLogs_LOGS";

				int tos_count_talendLogs_LOGS = 0;

				try {
					for (LogCatcherUtils.LogCatcherMessage lcm : talendLogs_LOGS.getMessages()) {
						row_talendLogs_LOGS.type = lcm.getType();
						row_talendLogs_LOGS.origin = (lcm.getOrigin() == null || lcm.getOrigin().length() < 1 ? null
								: lcm.getOrigin());
						row_talendLogs_LOGS.priority = lcm.getPriority();
						row_talendLogs_LOGS.message = lcm.getMessage();
						row_talendLogs_LOGS.code = lcm.getCode();

						row_talendLogs_LOGS.moment = java.util.Calendar.getInstance().getTime();

						row_talendLogs_LOGS.pid = pid;
						row_talendLogs_LOGS.root_pid = rootPid;
						row_talendLogs_LOGS.father_pid = fatherPid;

						row_talendLogs_LOGS.project = projectName;
						row_talendLogs_LOGS.job = jobName;
						row_talendLogs_LOGS.context = contextStr;

						/**
						 * [talendLogs_LOGS begin ] stop
						 */

						/**
						 * [talendLogs_LOGS main ] start
						 */

						currentVirtualComponent = "talendLogs_LOGS";

						currentComponent = "talendLogs_LOGS";

						tos_count_talendLogs_LOGS++;

						/**
						 * [talendLogs_LOGS main ] stop
						 */

						/**
						 * [talendLogs_LOGS process_data_begin ] start
						 */

						currentVirtualComponent = "talendLogs_LOGS";

						currentComponent = "talendLogs_LOGS";

						/**
						 * [talendLogs_LOGS process_data_begin ] stop
						 */

						/**
						 * [talendLogs_CONSOLE main ] start
						 */

						currentVirtualComponent = "talendLogs_CONSOLE";

						currentComponent = "talendLogs_CONSOLE";

						if (execStat) {
							runStat.updateStatOnConnection(iterateId, 1, 1

									, "Main"

							);
						}

///////////////////////		

						strBuffer_talendLogs_CONSOLE = new StringBuilder();

						if (row_talendLogs_LOGS.moment != null) { //

							strBuffer_talendLogs_CONSOLE.append(
									FormatterUtils.format_Date(row_talendLogs_LOGS.moment, "yyyy-MM-dd HH:mm:ss"));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.pid != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.pid));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.root_pid != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.root_pid));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.father_pid != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.father_pid));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.project != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.project));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.job != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.job));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.context != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.context));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.priority != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.priority));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.type != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.type));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.origin != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.origin));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.message != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.message));

						} //

						strBuffer_talendLogs_CONSOLE.append("|");

						if (row_talendLogs_LOGS.code != null) { //

							strBuffer_talendLogs_CONSOLE.append(String.valueOf(row_talendLogs_LOGS.code));

						} //

						if (globalMap.get("tLogRow_CONSOLE") != null) {
							consoleOut_talendLogs_CONSOLE = (java.io.PrintStream) globalMap.get("tLogRow_CONSOLE");
						} else {
							consoleOut_talendLogs_CONSOLE = new java.io.PrintStream(
									new java.io.BufferedOutputStream(System.out));
							globalMap.put("tLogRow_CONSOLE", consoleOut_talendLogs_CONSOLE);
						}
						consoleOut_talendLogs_CONSOLE.println(strBuffer_talendLogs_CONSOLE.toString());
						consoleOut_talendLogs_CONSOLE.flush();
						nb_line_talendLogs_CONSOLE++;
//////

//////                    

///////////////////////    			

						tos_count_talendLogs_CONSOLE++;

						/**
						 * [talendLogs_CONSOLE main ] stop
						 */

						/**
						 * [talendLogs_CONSOLE process_data_begin ] start
						 */

						currentVirtualComponent = "talendLogs_CONSOLE";

						currentComponent = "talendLogs_CONSOLE";

						/**
						 * [talendLogs_CONSOLE process_data_begin ] stop
						 */

						/**
						 * [talendLogs_CONSOLE process_data_end ] start
						 */

						currentVirtualComponent = "talendLogs_CONSOLE";

						currentComponent = "talendLogs_CONSOLE";

						/**
						 * [talendLogs_CONSOLE process_data_end ] stop
						 */

						/**
						 * [talendLogs_LOGS process_data_end ] start
						 */

						currentVirtualComponent = "talendLogs_LOGS";

						currentComponent = "talendLogs_LOGS";

						/**
						 * [talendLogs_LOGS process_data_end ] stop
						 */

						/**
						 * [talendLogs_LOGS end ] start
						 */

						currentVirtualComponent = "talendLogs_LOGS";

						currentComponent = "talendLogs_LOGS";

					}
				} catch (Exception e_talendLogs_LOGS) {
					globalMap.put("talendLogs_LOGS_ERROR_MESSAGE", e_talendLogs_LOGS.getMessage());
					logIgnoredError(String.format(
							"talendLogs_LOGS - tLogCatcher failed to process log message(s) due to internal error: %s",
							e_talendLogs_LOGS), e_talendLogs_LOGS);
				}

				ok_Hash.put("talendLogs_LOGS", true);
				end_Hash.put("talendLogs_LOGS", System.currentTimeMillis());

				/**
				 * [talendLogs_LOGS end ] stop
				 */

				/**
				 * [talendLogs_CONSOLE end ] start
				 */

				currentVirtualComponent = "talendLogs_CONSOLE";

				currentComponent = "talendLogs_CONSOLE";

//////
//////
				globalMap.put("talendLogs_CONSOLE_NB_LINE", nb_line_talendLogs_CONSOLE);

///////////////////////    			

				if (execStat) {
					runStat.updateStat(resourceMap, iterateId, 2, 0, "Main");
				}

				ok_Hash.put("talendLogs_CONSOLE", true);
				end_Hash.put("talendLogs_CONSOLE", System.currentTimeMillis());

				/**
				 * [talendLogs_CONSOLE end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			TalendException te = new TalendException(e, currentComponent, globalMap);

			te.setVirtualComponentName(currentVirtualComponent);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [talendLogs_LOGS finally ] start
				 */

				currentVirtualComponent = "talendLogs_LOGS";

				currentComponent = "talendLogs_LOGS";

				/**
				 * [talendLogs_LOGS finally ] stop
				 */

				/**
				 * [talendLogs_CONSOLE finally ] start
				 */

				currentVirtualComponent = "talendLogs_CONSOLE";

				currentComponent = "talendLogs_CONSOLE";

				/**
				 * [talendLogs_CONSOLE finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("talendLogs_LOGS_SUBPROCESS_STATE", 1);
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
		final FuzzyMatching FuzzyMatchingClass = new FuzzyMatching();

		int exitCode = FuzzyMatchingClass.runJobInTOS(args);

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
			java.io.InputStream inContext = FuzzyMatching.class.getClassLoader()
					.getResourceAsStream("cdc_demos/fuzzymatching_0_1/contexts/" + contextStr + ".properties");
			if (inContext == null) {
				inContext = FuzzyMatching.class.getClassLoader()
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

		this.globalResumeTicket = false;// to run others jobs

		try {
			errorCode = null;
			tFileInputDelimited_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_tFileInputDelimited_1) {
			globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", -1);

			e_tFileInputDelimited_1.printStackTrace();

		}

		this.globalResumeTicket = true;// to run tPostJob

		end = System.currentTimeMillis();

		if (watch) {
			System.out.println((end - startTime) + " milliseconds");
		}

		endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if (false) {
			System.out
					.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : FuzzyMatching");
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

	}

	private java.util.Map<String, Object> getSharedConnections4REST() {
		java.util.Map<String, Object> connections = new java.util.HashMap<String, Object>();

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
 * 118197 characters generated by Talend Open Studio for Data Integration on the
 * 12 septembre 2022 à 10:05:01 WEST
 ************************************************************************************************/