package sqlenv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.*;

import java.io.*;
import java.nio.charset.Charset;

class TJSONSQLEnv extends TSQLEnv {
    private String jsonfile;

    public TJSONSQLEnv(EDbVendor dbVendor, String jsonfile){
        super(dbVendor);
        this.jsonfile = jsonfile;
    }

    public void initSQLEnv(){
        String json = SQLUtil.getFileContent(jsonfile);
        if (json != null && json.trim().startsWith("{")) {
            JSONObject queryObject = JSON.parseObject(json);

            //JSONArray databases = queryObject.getJSONArray("databaseModel");
            JSONObject databaseModel = queryObject.getJSONObject("databaseModel");
            if (databaseModel != null){
                JSONArray databases = databaseModel.getJSONArray("databases");
                if (databases !=null){
                    //System.out.println("databases:"+databases.size());
                    for(int i=0;i<databases.size();i++){
                        JSONObject jsonDatabase = databases.getJSONObject(i);
                        String databaseName = jsonDatabase.getString("name");
                        JSONArray tables = jsonDatabase.getJSONArray("tables");
                        for(int j=0;j<tables.size();j++){
                            JSONObject jsonTable = tables.getJSONObject(j);
                            String schemeName = jsonTable.getString("schema");
                            String tableName = jsonTable.getString("name");
                            TSQLSchema sqlSchema = getSQLSchema(databaseName+"."+schemeName,true);
                            TSQLTable sqlTable = sqlSchema.createTable(tableName);
                            JSONArray columns = jsonTable.getJSONArray("columns");
                            for(int k=0;k<columns.size();k++){
                                JSONObject jsonColumn = columns.getJSONObject(k);
                                sqlTable.addColumn(jsonColumn.getString("name"));
                            }
                        }
                        //System.out.println("databases:"+databaseName+", tables:"+tables.size());
                    }
                }
            }

            JSONArray querys = queryObject.getJSONArray("queries");
            if (querys != null) {
                //System.out.println("queries:"+querys.size());
                for (int i = 0; i < querys.size(); i++) {
                    String sql = querys.getJSONObject(i).getString("sourceCode");
                    String schema = querys.getJSONObject(i).getString("schema");
                    String catalog = querys.getJSONObject(i).getString("database");
                    String procedureName = querys.getJSONObject(i).getString("name");
                    String sourceCode = querys.getJSONObject(i).getString("sourceCode");

                    TSQLSchema sqlSchema = getSQLSchema(catalog+"."+schema,true);
                    TSQLProcedure procedure = sqlSchema.createProcedure(procedureName);
                    if (procedure != null){
                        procedure.setDefinition(sourceCode);
                    }else{
                        // this script is used to create view
                        TSQLTable table = sqlSchema.createTable(procedureName);
                        table.setView(true);
                        table.setDefinition(sourceCode);

                    }
                    // SQLUtil.writeFileContent(new File(targetDir, (i + 1) + ".sql"), sql);
                }
            }

        } else {
            System.out.println(jsonfile + " is not a valid json file.");
        }
    }
}

class SQLUtil {

    public static String getFileContent(File file) {
        String charset = null;
        String sqlfilename = file.getAbsolutePath();
        int read = 0;
        try {
            FileInputStream fr = new FileInputStream(sqlfilename);
            byte[] bom = new byte[4];
            fr.read(bom, 0, bom.length);

            if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
                    && (bom[3] == (byte) 0xFF)) {
                charset = "UTF-32BE";
                read = 4;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
                    && (bom[3] == (byte) 0x00)) {
                charset = "UTF-32LE";
                read = 4;
            } else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
                charset = "UTF-8";
                read = 3;
            } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
                charset = "UTF-16BE";
                read = 2;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
                charset = "UTF-16LE";
                read = 2;
            } else {
                charset = Charset.defaultCharset().name();
                read = 0;
            }

            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(sqlfilename));
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] content = new byte[filelength.intValue() - read];
        System.arraycopy(filecontent, read, content, 0, content.length);

        try {
            String fileContent = new String(content, charset == null ? Charset.defaultCharset().name() : charset)
                    .trim();
            return fileContent;
        } catch (UnsupportedEncodingException e) {
            System.err
                    .println("The OS does not support " + charset == null ? Charset.defaultCharset().name() : charset);
            return null;
        }
    }

    public static String getInputStreamContent(InputStream is, boolean close) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] tmp = new byte[4096];
            while (true) {
                int r = is.read(tmp);
                if (r == -1)
                    break;
                out.write(tmp, 0, r);
            }
            byte[] bytes = out.toByteArray();
            if (close) {
                is.close();
            }
            out.close();
            String content = new String(bytes);
            return content.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileContent(String filePath) {
        if (filePath == null)
            return "";
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory())
            return "";
        return getFileContent(file);
    }

    /**
     * @param file the filePath
     * @return the UTFFileHandler.UnicodeInputStream
     * @throws Exception
     */
    public static InputStream getInputStreamWithoutBom(String file) throws IOException {
        UnicodeInputStream stream = null;
        FileInputStream fis = new FileInputStream(file);
        stream = new UnicodeInputStream(fis, null);
        return stream;
    }

    /**
     * This inputstream will recognize unicode BOM marks and will skip bytes if
     * getEncoding() method is called before any of the read(...) methods.
     * <p>
     * Usage pattern: String enc = "ISO-8859-1"; // or NULL to use systemdefault
     * FileInputStream fis = new FileInputStream(file); UnicodeInputStream uin = new
     * UnicodeInputStream(fis, enc); enc = uin.getEncoding(); // check and skip
     * possible BOM bytes InputStreamReader in; if (enc == null) in = new
     * InputStreamReader(uin); else in = new InputStreamReader(uin, enc);
     */
    public static class UnicodeInputStream extends InputStream {
        PushbackInputStream internalIn;
        boolean isInited = false;
        String defaultEnc;
        String encoding;

        private static final int BOM_SIZE = 4;

        public UnicodeInputStream(InputStream in, String defaultEnc) {
            internalIn = new PushbackInputStream(in, BOM_SIZE);
            this.defaultEnc = defaultEnc;
        }

        public String getDefaultEncoding() {
            return defaultEnc;
        }

        public String getEncoding() {
            if (!isInited) {
                try {
                    init();
                } catch (IOException ex) {
                    IllegalStateException ise = new IllegalStateException("Init method failed.");
                    ise.initCause(ise);
                    throw ise;
                }
            }
            return encoding;
        }

        /**
         * Read-ahead four bytes and check for BOM marks. Extra bytes are unread back to
         * the stream, only BOM bytes are skipped.
         */
        protected void init() throws IOException {
            if (isInited)
                return;

            byte bom[] = new byte[BOM_SIZE];
            int n, unread;
            n = internalIn.read(bom, 0, bom.length);

            if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
                    && (bom[3] == (byte) 0xFF)) {
                encoding = "UTF-32BE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
                    && (bom[3] == (byte) 0x00)) {
                encoding = "UTF-32LE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
                encoding = "UTF-8";
                unread = n - 3;
            } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
                encoding = "UTF-16BE";
                unread = n - 2;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
                encoding = "UTF-16LE";
                unread = n - 2;
            } else {
                encoding = defaultEnc;
                unread = n;
            }

            if (unread > 0)
                internalIn.unread(bom, (n - unread), unread);

            isInited = true;
        }

        public void close() throws IOException {
            internalIn.close();
        }

        public int read() throws IOException {
            return internalIn.read();
        }
    }

    public static void writeFileContent(File file, String sql) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
            pw.write(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

    }
}