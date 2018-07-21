library globals;

import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

//Variables
bool isLoggedIn = false;
String token = "";
String domain = "";
String apiURL = "http://192.168.0.74:8000/login";
String error = "";

String id = "0";
String firstname = "";
String email = "";
String avatar = "https://api.adorable.io/avatars/128/BEAM-Messenger.png";

class Utility {
  static Future<Null> showAlertPopup(
      BuildContext context, String title, String detail) async {
    return showDialog<Null>(
      context: context,
      barrierDismissible: false, // user must tap button!
      child: new AlertDialog(
        title: new Text(title),
        content: new SingleChildScrollView(
          child: new ListBody(
            children: <Widget>[
              new Text(detail),
            ],
          ),
        ),
        actions: <Widget>[
          new FlatButton(
            child: new Text('Done'),
            onPressed: () {
              Navigator.of(context).pop();
            },
          ),
        ],
      ),
    );
  }

  static Future<String> getData(Map params) async {
    var requestURL = apiURL;
    requestURL = requestURL;
    print("Request URL: " + requestURL);

    var url = requestURL;
    String result;

    try {
      await http.post(url, body: {
        "email": params["email"],
        "password": params["password"]
      }).then((response) {
        result = response.body;
        print('Answer: ' + result.toString());
      });
    } catch (exception) {
      result = 'Failed logging in';
    }

    return result;
  }

  static Widget newTextButton(String title, VoidCallback onPressed) {
    return new FlatButton(
      child: new Text(title,
          textAlign: TextAlign.center,
          style: const TextStyle(
              color: Colors.black,
              fontSize: 14.0,
              fontFamily: "Roboto",
              fontWeight: FontWeight.bold)),
      onPressed: onPressed,
    );
  }
}
