import 'package:flutter/material.dart';
import 'package:battery_meter/screens/home.dart';
import 'package:battery_meter/screens/manage_session.dart';

void main() => runApp(MaterialApp(
  title: 'Battery Meter',
  theme: ThemeData(
    primarySwatch: Colors.lightGreen
  ),
  initialRoute: '/',
  routes: {
    '/': (context) => HomeScreen(),
    '/manage-session': (context) => ManageSessionScreen()
  }
));


