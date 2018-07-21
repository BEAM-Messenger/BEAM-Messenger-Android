library globals;

import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';

//Variables
bool isLoggedIn = false;
String token = "";
String domain = "";
String apiURL = "http://192.168.0.74:8000/login";
String error = "";

String id = "0";
String firstname = "";
String email = "";
String avatar ="https://api.adorable.io/avatars/128/BEAM-Messenger.png";

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

  static Future<String> getData(String params) async {
    var requestURL = apiURL;
    requestURL = requestURL + params;
//    requestURL = requestURL + "calltype=" + calltypeParm;
//    requestURL = requestURL + "&mod=" + modParm;
//    requestURL = requestURL + "&?action=" + actionParm;
//    requestURL = requestURL + "&?param=" + paramsParm;
//    requestURL = requestURL + "&?foo=" + fooParm;
    print("Request URL: " + requestURL);

    var url = requestURL;
    var httpClient = new HttpClient();
    String result;
    try {
      var request = await httpClient.getUrl(Uri.parse(url));
      var response = await request.close();
      if (response.statusCode == HttpStatus.OK) {
        try {
          var json = await response.transform(UTF8.decoder).join();
          result = json;
        } catch (exception) {
          result = 'Error Getting Data';
        }
      } else {
        result =
            'Error getting IP address:\nHttp status ${response.statusCode}';
      }
    } catch (exception) {
      result = 'Failed getting IP address';
    }
    print("Result: " + result);
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
