package com.example.donorschoose;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class for JavaMail API functions
 */
public class JavaMailAPI extends AsyncTask {

    private FirebaseFirestore db;

    private String recipient, subject, message;
    private String sender = "donorschoosebd@gmail.com";     // donors choose official email
    private String password;                                // to be loaded from firebase

    /**
     * Constructor for email
     * @param recipient is the email address of the charity to which the email wil be delivered
     * @param subject is the subject of the email
     * @param message is the body of the email
     */
    public JavaMailAPI(String recipient, String subject, String message)
    {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;

        db = FirebaseFirestore.getInstance();   // need to load password from database
        db.collection("authCodes").document("email").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                password = documentSnapshot.getString("code");  // donors choose email's app password
            }
        });
    }

    /**
     * Sending the email
     * @param objects is unused in the implementation
     * @return null
     */
    @Override
    protected Object doInBackground(Object[] objects) {
        Properties properties = new Properties();               // creating smtp connection with Gmail
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        // connecting using donors choose credentials
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {    // connecting using donors choose credentials
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);     // attempting to send email
        try {
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
