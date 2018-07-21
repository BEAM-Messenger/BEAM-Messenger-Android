import 'package:meta/meta.dart';
import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:beam_messenger/globals.dart' as globals;
import 'lockedscreen/home.dart';
import 'pincode/pincode_verify.dart';
import 'pincode/pincode_create.dart';
import 'package:local_auth/local_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'components.dart';

void main() {
  runApp(new MaterialApp(home: new LoginPage()));
}

class LoginPage extends StatefulWidget {
  LoginPageState createState() => new LoginPageState();
}

class LoginPageState extends State<LoginPage> {
  final formKey = new GlobalKey<FormState>();
  final _scaffoldKey = new GlobalKey<ScaffoldState>();

  String _email;
  String _password;
  bool _usePinCode = false;

  bool autovalidate = false;
  void _handleSubmitted() {
    final FormState form = formKey.currentState;
    if (!form.validate()) {
      autovalidate = true; // Start validating on every change.
      showInSnackBar('Please fix the errors in red before submitting.');
      setState(() {
        globals.isLoggedIn = false;
      });
    } else {
      form.save();
      _performLogin();
    }
  }

  void showInSnackBar(String value) {
    _scaffoldKey.currentState
        .showSnackBar(new SnackBar(content: new Text(value)));
  }

  void _performLogin() async {
    // This is just a demo, so no actual login here.
    final snackbar = new SnackBar(
      duration: new Duration(seconds: 10),
      content: new Row(
        children: <Widget>[
          new CircularProgressIndicator(),
          new Text("  Signing-In...")
        ],
      ),
    );
    _scaffoldKey.currentState.showSnackBar(snackbar);
    await tryLogin(_email, _password);
    if (globals.isLoggedIn) {
      // await showAlertPopup();
      // await saveData(_usePinCode);
      if (_usePinCode) {
        navigateToScreen('Create Pin');
      } else {
        navigateToScreen('Home');
      }
    }
    _scaffoldKey.currentState.hideCurrentSnackBar();
  }

  Future<bool> _loginRequest(String email, String password) async {
    String result = "";
    result = await globals.Utility.getData("");

    //Decode Data
    try {
      Map decoded = JSON.decode(result);
      globals.id = decoded["user_data"]['id'].toString();
      globals.firstname = decoded["user_data"]['name'].toString();
      globals.email = decoded["user_data"]['email'].toString();
      globals.avatar = decoded["user_data"]['avatar'].toString();

      print(globals.id);
      print(globals.firstname);
      print(globals.email);
      print(globals.avatar);

      globals.token = globals.id;
    } catch (exception) {
      print("Error Decoding Data");
      return false;
    }
    return true;
  }

  tryLogin(String email, String password) async {
    await _loginRequest(email, password);
    print("Token: " + globals.token + " | Error: " + globals.error);

    if (globals.token != 'null') {
      print("Valid Token!");
      setState(() {
        globals.isLoggedIn = true;
      });

      //Save Email and Password to Shared Preferences
      SharedPreferences prefs = await SharedPreferences.getInstance();
      print('Email: $email Password $password.');
      prefs.setString('userEmail', email);
      prefs.setString('userPassword', password);
      prefs.setString('userToken', globals.token);
    } else {
      print("Invalid Token!");
      setState(() {
        globals.isLoggedIn = false;
      });
      globals.error = "Check Email and Password!";
      globals.Utility.showAlertPopup(
          context, "Info", "Please Try Logging In Again! \n" + globals.error);
    }
  }

  saveData(bool usePin) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    if (usePin) {
      prefs.setBool('usePinCode', true);
    } else {
      prefs.setBool('usePinCode', false);
    }
  }

  Future<Null> navigateToScreen(String name) async {
    if (name.contains('Home')) {
      Navigator.push(
        context,
        new MaterialPageRoute(
            builder: (context) =>
                new Home()), //When Authorized Navigate to the next screen
      );
    } else if (name.contains('Create Pin')) {
      Navigator.push(
        context,
        new MaterialPageRoute(
            builder: (context) =>
                new PinCodeCreate()), //When Authorized Navigate to the next screen
      );
    } else if (name.contains('Verify Pin')) {
      Navigator.push(
        context,
        new MaterialPageRoute(
            builder: (context) =>
                new PinCodeVerify()), //When Authorized Navigate to the next screen
      );
    } else {
      print('Error: $name');
    }
  }

  Future<Null> showAlertPopup() async {
    return showDialog<Null>(
      context: context,
      barrierDismissible: false, // user must tap button!
      child: new AlertDialog(
        title: new Text('Info'),
        content: new SingleChildScrollView(
          child: new ListBody(
            children: <Widget>[
              new Text('Would you like to set a Pin Code for a faster log in?'),
              new Text('Once a Pin is set you can unlock with biometrics'),
            ],
          ),
        ),
        actions: <Widget>[
          new FlatButton(
            child: new Text('Yes'),
            onPressed: () {
              _usePinCode = true;
              Navigator.of(context).pop();
            },
          ),
          new FlatButton(
            child: new Text('No'),
            onPressed: () {
              _usePinCode = false;
              Navigator.of(context).pop();
            },
          ),
        ],
      ),
    );
  }

  String _authorized = 'Not Authorized';
  Future<Null> _goToBiometrics() async {
    String email;
    String password;
    final LocalAuthentication auth = new LocalAuthentication();
    bool authenticated = false;
    try {
      authenticated = await auth.authenticateWithBiometrics(
          localizedReason: 'Scan your fingerprint to authenticate',
          useErrorDialogs: true,
          stickyAuth: false);
    } catch (e) {
      print(e);
    }
    if (!mounted) return;

    setState(() {
      _authorized = authenticated ? 'Authorized' : 'Not Authorized';
      print(_authorized);

      if (authenticated) {
        //Todo: Get Saved Email and Password from Shared Preferences
        //https://github.com/flutter/plugins/tree/master/packages/shared_preferences
        String savedEmail = "Test";
        String savedPassword = "Test";
        //Todo: Get Email and Password from Shared Preferences
        email = savedEmail;
        password = savedPassword;
      }
    });
    if (authenticated) {
      await tryLogin(email, password);
      if (globals.isLoggedIn) {
        navigateToScreen('Home');
      } else {
        globals.Utility.showAlertPopup(
            context, "Info", "Login Failed\nPlease Try Logging In Again");
      }
    } else {
      globals.Utility.showAlertPopup(
          context, "Info", "Login Failed\nPlease Try Biometrics Again");
    }
  }

  void newAccount() {
    Navigator.of(context).push(new MaterialPageRoute<Null>(
        builder: (BuildContext context) {
          return new NewAccountPage();
        },
        fullscreenDialog: true));
  }

  void needHelp() {
    Navigator.of(context).push(new MaterialPageRoute<Null>(
        builder: (BuildContext context) {
          return new HelpPage();
        },
        fullscreenDialog: true));
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      key: _scaffoldKey,
      appBar: new AppBar(
        title: new Text('Login'),
      ),
      body: new Container(
        color: Colors.grey[300],
        child: new ListView(
          physics: new AlwaysScrollableScrollPhysics(),
          key: new PageStorageKey("Divider 1"),
          children: <Widget>[
            new Container(
              height: 20.0,
            ),
            new Padding(
              padding: EdgeInsets.all(20.0),
              child: new Card(
                child: new Column(
                  children: <Widget>[
                    new Container(height: 30.0),
                    new Icon(
                      globals.isLoggedIn ? Icons.lock_open : Icons.lock_outline,
                      size: 120.0,
                    ),
                    new Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: new Form(
                        key: formKey,
                        child: new Column(
                          children: [
                            new TextFormField(
                              decoration:
                                  new InputDecoration(labelText: 'Email'),
                              validator: (val) =>
                                  val.length < 1 ? 'Email Required' : null,
                              onSaved: (val) => _email = val,
                              obscureText: false,
                              keyboardType: TextInputType.text,
                              autocorrect: false,
                            ),
                            new Container(height: 10.0),
                            new TextFormField(
                              decoration:
                                  new InputDecoration(labelText: 'Password'),
                              validator: (val) =>
                                  val.length < 1 ? 'Password Required' : null,
                              onSaved: (val) => _password = val,
                              obscureText: true,
                              keyboardType: TextInputType.text,
                              autocorrect: false,
                            ),
                            new Container(height: 5.0),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            new Padding(
              padding: EdgeInsets.all(20.0),
              child: new Row(
                children: <Widget>[
                  new Expanded(
                    child: new Padding(
                      padding: const EdgeInsets.all(5.0),
                      child: new RaisedButton(
                        color: Colors.blue,
                        onPressed: _handleSubmitted,
                        child: new Text(
                          'Login',
                          style: new TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
                  ),
                  new Padding(
                    padding: const EdgeInsets.all(5.0),
                    child: new RaisedButton(
                      color: Colors.redAccent[400],
                      onPressed: _goToBiometrics,
                      child: new Icon(
                        Icons.fingerprint,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            new Padding(
              padding: EdgeInsets.all(20.0),
              child: new Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  new TextButton(name: "Create Account", onPressed: newAccount),
                  new TextButton(name: "Need Help?", onPressed: needHelp),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}

class NewAccountPage extends StatefulWidget {
  @override
  NewAccountPageState createState() => new NewAccountPageState();
}

class NewAccountPageState extends State<NewAccountPage> {
  final formKey = new GlobalKey<FormState>();
  final _scaffoldKey = new GlobalKey<ScaffoldState>();

  String _email;
  String _password;

  void openTermsAndConditions() {
    Navigator.of(context).push(new MaterialPageRoute<Null>(
        builder: (BuildContext context) {
          return new TermsConditionsPage();
        },
        fullscreenDialog: true));
  }

  bool isEmail(String email) {
    String regex =
        r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$';
    RegExp regExp = new RegExp(regex);
    return regExp.hasMatch(email);
  }

  bool autovalidate = false;
  void _handleSubmitted() {
    final FormState form = formKey.currentState;
    if (!form.validate()) {
      autovalidate = true; // Start validating on every change.
      showInSnackBar('Please fix the errors before submitting again.');
    } else {
      form.save();
      Navigator.pop(context);
    }
  }

  void showInSnackBar(String value) {
    _scaffoldKey.currentState
        .showSnackBar(new SnackBar(content: new Text(value)));
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      key: _scaffoldKey,
      appBar: new AppBar(
        title: const Text('Create New Account'),
      ),
      body: new Container(
        color: Colors.grey[300],
        child: new ListView(
          physics: new AlwaysScrollableScrollPhysics(),
          key: new PageStorageKey("Divider 1"),
          children: <Widget>[
            new Container(
              height: 20.0,
            ),
            new Padding(
              padding: EdgeInsets.all(20.0),
              child: new Card(
                child: new Column(
                  children: <Widget>[
                    new Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: new Form(
                        key: formKey,
                        child: new Column(
                          children: [
                            new TextFormField(
                              decoration:
                                  new InputDecoration(labelText: 'Email'),
                              validator: (val) => isEmail(val) && val.length < 1
                                  ? 'Email Required'
                                  : null,
                              onSaved: (val) => _email = val,
                              obscureText: false,
                              keyboardType: TextInputType.emailAddress,
                              autocorrect: false,
                            ),
                            new Container(
                              height: 10.0,
                            ),
                            new TextFormField(
                              decoration:
                                  new InputDecoration(labelText: 'Email'),
                              validator: (val) =>
                                  isEmail(val) && val.length < 1 ? 'Email Required' : null,
                              onSaved: (val) => _email = val,
                              obscureText: true,
                              keyboardType: TextInputType.emailAddress,
                              autocorrect: false,
                            ),
                            new Container(height: 10.0),
                            new TextFormField(
                              decoration:
                                  new InputDecoration(labelText: 'Password'),
                              validator: (val) =>
                                  val.length >= 8 && val.length >= 32 ? 'Password length must be between 8 and 32 characters.' : null,
                              onSaved: (val) => _password = val,
                              obscureText: true,
                              keyboardType: TextInputType.text,
                              autocorrect: false,
                            ),
                            new Container(
                              height: 10.0,
                            ),
                            new TextFormField(
                              decoration:
                                  new InputDecoration(labelText: 'Password'),
                              validator: (val) =>
                                  val.length >= 8 && val.length >= 32 ? 'Password length must be between 8 and 32 characters.' : null,
                              onSaved: (val) => _password = val,
                              obscureText: true,
                              keyboardType: TextInputType.text,
                              autocorrect: false,
                            ),
                            new Container(height: 5.0),
                          ],
                        ),
                      ),
                    ),
                    new TextButton(
                        name: "Terms and Conditions",
                        onPressed: openTermsAndConditions),
                  ],
                ),
              ),
            ),
            new Padding(
              padding: EdgeInsets.all(20.0),
              child: new Row(
                children: <Widget>[
                  new Expanded(
                    child: new Padding(
                      padding: const EdgeInsets.all(5.0),
                      child: new RaisedButton(
                        color: Colors.blue,
                        onPressed: _handleSubmitted,
                        child: new Text(
                          'Save',
                          style: new TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class HelpPage extends StatefulWidget {
  @override
  HelpPageState createState() => new HelpPageState();
}

class HelpPageState extends State<HelpPage> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: const Text('Help'),
      ),
      body: new Center(
        child: new Column(
          children: <Widget>[
            new Padding(
              padding: new EdgeInsets.all(10.0),
              child: new Text(
                '24/7 Customer Support',
                textAlign: TextAlign.center,
                style: new TextStyle(
                  color: Colors.black,
                  fontWeight: FontWeight.bold,
                  fontSize: 20.0,
                ),
              ),
            ),
            new Padding(
              padding: new EdgeInsets.all(10.0),
              child: new Text(
                'Email: contact@beam-messenger.com',
                textAlign: TextAlign.center,
                style: new TextStyle(
                  color: Colors.black,
                  fontSize: 15.0,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class TermsConditionsPage extends StatefulWidget {
  @override
  TermsConditionsPageState createState() => new TermsConditionsPageState();
}

class TermsConditionsPageState extends State<TermsConditionsPage> {
  String termsOfUse = "Your data is secure - don't worry :) (TODO: Write TOU)";
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: const Text('Terms and Conditions'),
      ),
      body: new Center(
        child: new Column(
          children: <Widget>[
            new Padding(
              padding: new EdgeInsets.all(10.0),
              child: new Text(
                termsOfUse,
                textAlign: TextAlign.center,
                style: new TextStyle(
                  color: Colors.black,
                  fontSize: 15.0,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
