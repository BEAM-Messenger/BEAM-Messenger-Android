import 'dart:async';
import 'dart:convert'; // not needed for later use

import 'package:beam_messenger/utils/network_util.dart';
import 'package:beam_messenger/models/user.dart';

class RestDatasource {
  NetworkUtil _netUtil = new NetworkUtil();
  static final BASE_URL = "http://192.168.0.74:8000";
  static final LOGIN_URL = BASE_URL + "/login";

  Future<User> login(String email, String password) {
    return _netUtil.post(LOGIN_URL,
        body: {"email": email, "password": password}).then((dynamic res) {
      print(res.toString());
      if (res["status"]) throw new Exception(res["message"]);
      return jsonDecode(
          "{ error: false, user: { email: “marvin@borners.de”, password: “password” } }"); // later: access token
      // return new User.map(res["user"]);
    });
  }
}
