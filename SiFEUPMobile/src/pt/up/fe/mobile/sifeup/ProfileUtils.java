package pt.up.fe.mobile.sifeup;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Employee;
import pt.up.fe.mobile.datatypes.Student;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ProfileUtils {
    private ProfileUtils() {
    }

    public static AsyncTask<String, Void, ERROR_TYPE> getStudentReply(
            String code, ResponseCommand command) {
        return new FetcherTask(command, new StudentParser()).execute(SifeupAPI
                .getStudentUrl(code));
    }

    public static AsyncTask<String, Void, ERROR_TYPE> getEmployeeReply(
            String code, ResponseCommand command) {
        return new FetcherTask(command, new EmployeeParser()).execute(SifeupAPI
                .getEmployeeUrl(code));
    }

    public static AsyncTask<String, Void, ERROR_TYPE> getPersonPic(String code,
            ResponseCommand command) {
        return new PicFetcher(command).execute(code);
    }

    /**
     * Parses a JSON String containing Exams info, Stores that info at
     * Collection exams.
     */

    private static class StudentParser implements ParserCommand {

        public Object parse(String page) {
            try {
                SessionManager.getInstance().loadFriends();
                Student me = new Student();
                JSONObject jObject = new JSONObject(page);
                SifeupUtils.removeEmptyKeys(jObject);
                if (jObject.has("codigo"))
                    me.setCode(jObject.getString("codigo"));
                if (jObject.has("nome"))
                    me.setName(jObject.getString("nome"));
                if (jObject.has("curso_sigla"))
                    me.setProgrammeAcronym(jObject.getString("curso_sigla"));
                if (jObject.has("curso_nome"))
                    me.setProgrammeName(jObject.getString("curso_nome"));
                if (jObject.has("ano_lect_matricula"))
                    me.setRegistrationYear(jObject
                            .getString("ano_lect_matricula"));
                if (jObject.has("estado"))
                    me.setState(jObject.getString("estado"));
                if (jObject.has("ano_curricular"))
                    me.setAcademicYear(jObject.getString("ano_curricular"));
                if (jObject.has("email"))
                    me.setEmail(jObject.getString("email"));
                if (jObject.has("email_alternativo"))
                    me.setEmailAlt(jObject.getString("email_alternativo"));
                if (jObject.has("telemovel"))
                    me.setMobile(jObject.getString("telemovel"));
                if (jObject.has("telefone"))
                    me.setTelephone(jObject.getString("telefone"));
                if (jObject.has("ramo"))
                    me.setBranch(jObject.getString("ramo"));
                return me;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private static class EmployeeParser implements ParserCommand {

        public Object parse(String page) {
            try {
                SessionManager.getInstance().loadFriends();
                return new Employee().JSONSubject(page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private static class PicFetcher extends
            AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {
        private final ResponseCommand command;
        private Bitmap bitmap = null;

        private PicFetcher(ResponseCommand com) {
            command = com;
        }

        protected void onPostExecute(ERROR_TYPE result) {
            if (result == null) {
                command.onResultReceived(bitmap);
                return;
            }
            command.onError(result);
        }

        protected ERROR_TYPE doInBackground(String... code) {
            HttpsURLConnection httpConn = SifeupAPI
                    .getUncheckedConnection(SifeupAPI.getPersonPicUrl(code[0]));
            httpConn.setRequestProperty("Cookie", SessionManager.getInstance()
                    .getCookie());
            try {
                httpConn.connect();

                try {
                    BufferedInputStream bis = new BufferedInputStream(
                            httpConn.getInputStream());
                    ByteArrayBuffer baf = new ByteArrayBuffer(50);
                    int read = 0;
                    int bufSize = 512;
                    byte[] buffer = new byte[bufSize];
                    while (true) {
                        read = bis.read(buffer);
                        if (read == -1) {
                            break;
                        }
                        baf.append(buffer, 0, read);
                    }
                    bis.close();
                    httpConn.getInputStream().close();
                    bitmap = BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
                            baf.length());
                } catch (IOException e) {
                    e.printStackTrace();
                    return ERROR_TYPE.GENERAL;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ERROR_TYPE.NETWORK;
            } finally {
                httpConn.disconnect();
            }
            return null;
        }
    }

}
