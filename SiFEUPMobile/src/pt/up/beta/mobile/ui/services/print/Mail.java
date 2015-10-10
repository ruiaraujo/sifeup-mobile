package pt.up.beta.mobile.ui.services.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import pt.up.mobile.BuildConfig;

public class Mail extends javax.mail.Authenticator {
	private String user;
	private String pass;

	private String[] to;
	private String from;

	private String port;
	private String sport;

	private String host;

	private String subject;
	private String body;

	private boolean auth;

	private boolean debuggable;

	private Multipart multipart;

	public Mail() {
		host = "smtp.fe.up.pt"; // default smtp server
		port = "465"; // default smtp port
		sport = "465"; // default socketfactory port

		user = ""; // username
		pass = ""; // password
		from = ""; // email sent from
		subject = ""; // email subject
		body = ""; // email body

		debuggable = BuildConfig.DEBUG; // debug mode on or off - default true
										// in build mode
		auth = true; // smtp authentication - default on

		multipart = new MimeMultipart();

		// There is something wrong with MailCap, javamail can not find a
		// handler for the multipart/mixed part, so this bit needs to be added.
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap
				.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
	}

	public Mail(String user, String pass) {
		this();
		this.user = user;
		this.pass = pass;
	}

	public boolean send() throws Exception {
		Properties props = _setProperties();

		if (!user.equals("") && !pass.equals("") && to.length > 0
				&& !from.equals("")) {
			Session session = Session.getInstance(props, this);

			MimeMessage msg = new MimeMessage(session);

			msg.setSender(new InternetAddress(from));
			msg.setFrom(new InternetAddress(from));

			InternetAddress[] addressTo = new InternetAddress[to.length];
			for (int i = 0; i < to.length; i++) {
				addressTo[i] = new InternetAddress(to[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

			msg.setSubject(subject);
			msg.setSentDate(new Date());

			// setup message body
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
			multipart.addBodyPart(messageBodyPart);

			// Put parts in message
			msg.setContent(multipart);

			// send email
			Transport.send(msg);

			return true;
		} else {
			return false;
		}
	}

	public void addAttachment(final InputStream is, final String filename)
			throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new DataSource() {

			@Override
			public OutputStream getOutputStream() throws IOException {
				return null;
			}

			@Override
			public String getName() {
				return filename;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return is;
			}

			@Override
			public String getContentType() {
				return "application/octet-stream";
			}
		};
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		multipart.addBodyPart(messageBodyPart);
	}

	public void addAttachment(String filename) throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		multipart.addBodyPart(messageBodyPart);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, pass);
	}

	private Properties _setProperties() {
		Properties props = new Properties();

		props.put("mail.smtp.host", host);

		if (debuggable) {
			props.put("mail.debug", "true");
		}

		if (auth) {
			props.put("mail.smtp.auth", "true");
		}

		props.put("mail.smtp.port", port);
		props.put("mail.smtp.socketFactory.port", sport);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		return props;
	}

	// the getters and setters
	public String getBody() {
		return body;
	}

	public void setBody(String _body) {
		this.body = _body;
	}

	public String get_user() {
		return user;
	}

	public void set_user(String _user) {
		this.user = _user;
	}

	public String get_pass() {
		return pass;
	}

	public void set_pass(String _pass) {
		this.pass = _pass;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] _to) {
		this.to = _to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String _from) {
		this.from = _from;
	}

	public String get_port() {
		return port;
	}

	public void set_port(String _port) {
		this.port = _port;
	}

	public String get_sport() {
		return sport;
	}

	public void set_sport(String _sport) {
		this.sport = _sport;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String _host) {
		this.host = _host;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String _subject) {
		this.subject = _subject;
	}

	public boolean iAuth() {
		return auth;
	}

	public void setAuth(boolean _auth) {
		this.auth = _auth;
	}

	public boolean is_debuggable() {
		return debuggable;
	}

	public void set_debuggable(boolean _debuggable) {
		this.debuggable = _debuggable;
	}

	public Multipart get_multipart() {
		return multipart;
	}

	public void set_multipart(Multipart _multipart) {
		this.multipart = _multipart;
	}

	// more of the getters and setters …..
}
