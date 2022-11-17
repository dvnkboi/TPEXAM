package com.example.tpexam.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.example.tpexam.Model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";

    public interface SmsBroadcastListener {
        void onCreatePerson(Person p);

        void onDeletePerson(Person p);

        void onEditPerson(Person p);
    }

    private static SmsBroadcastListener listener;
    private static SmsBroadcastReceiver receiver;

    public static void setContext(Context context) {
        try {
            listener = (SmsBroadcastReceiver.SmsBroadcastListener) context;
            receiver = new SmsBroadcastReceiver();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getClass()
                    + " must implement SmsBroadcastListener");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), SMS_RECEIVED)) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage smsMessage = messages[0];
            Person p = parsePersonString(smsMessage.getMessageBody());
            Log.d(TAG, p.toString());
            if (smsMessage.getMessageBody().toLowerCase().startsWith("create")) {
                listener.onCreatePerson(p);
            } else if (smsMessage.getMessageBody().toLowerCase().startsWith("edit")) {
                listener.onEditPerson(p);
            } else if (smsMessage.getMessageBody().toLowerCase().startsWith("delete")) {
                listener.onDeletePerson(p);
            }
        }
    }

    private Person parsePersonString(String personCommandString) {
        ArrayList<String> personCommandArr = Arrays.stream(personCommandString.split("\n"))
                .skip(1)
                .collect(Collectors.toCollection(ArrayList<String>::new));

        Person result = new Person();
        try {
            for (String personCommandPart : personCommandArr) {
                ArrayList<String> keyVal = Arrays
                        .stream(personCommandPart.split(":"))
                        .map(String::trim)
                        .collect(Collectors.toCollection(ArrayList<String>::new));

                if (keyVal.size() == 2 && !keyVal.get(1).equals("")) {
                    String key = keyVal.get(0).toLowerCase();
                    String value = keyVal.get(1);
                    switch (key) {
                        case "first name":
                            result.setFirstName(value);
                            break;
                        case "last name":
                            result.setLastName(value);
                            break;
                        case "email":
                            result.setEmail(value);
                            break;
                        case "age":
                            result.setAge(Integer.parseInt(value));
                            break;
                        case "id":
                            result.setId(Integer.parseInt(value));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not parse person string from sms \n");
            e.printStackTrace();
        }
        return result;
    }
}