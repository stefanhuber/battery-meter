import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

final _formKey = GlobalKey<FormState>();
String _session_name = "";
int _session_interval = 0;

class ManageSessionScreen extends StatelessWidget {
  static const MethodChannel methodChannel = MethodChannel("METERING");

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Start Metering Session'),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.add),
            tooltip: "Start new Metering Session",
            onPressed: () async {
              if (_formKey.currentState.validate()) {
                try {
                  _formKey.currentState.save();
                  await methodChannel.invokeMethod("startMeteringSession", <String, dynamic>{
                    "name": _session_name,
                    "interval": _session_interval
                  });
                  Navigator.pushNamedAndRemoveUntil(context, "/", (Route<dynamic> route) => false);
                } on PlatformException catch (e) {
                  // try again ?
                }
              }
            },
          )
        ],
      ),
      body: Form(
        key: _formKey,
        child: Column(
          children: <Widget>[
            TextFormField(
              validator: (value) {
                if (value.isEmpty) {
                  return "Please enter a session name";
                }
              },
              onSaved: (String value) {
                _session_name = value;
              },
              decoration: const InputDecoration(
                contentPadding: const EdgeInsets.all(16),
                hintText: 'Enter a name for the metering session',
                labelText: 'Metering session name',
              ),
            ),
            TextFormField(
              keyboardType: TextInputType.numberWithOptions(),
              onSaved: (String value) {
                _session_interval = int.parse(value);
              },
              decoration: const InputDecoration(
                contentPadding: const EdgeInsets.all(16.0),
                hintText: 'Enter a metering interval in seconds',
                labelText: 'Metering session interval',
              ),
            )
          ],
        ),
      )
    );
  }
}