import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:battery_meter/models/session.dart';

class HomeScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => new HomeScreenState();
}

class HomeScreenState extends State<HomeScreen> {
  static const MethodChannel methodChannel = MethodChannel("METERING");

  List<Session> _result = [];
  bool _canAdd = true;

  @override
  void initState() {
    loadData();
  }

  void loadData() {
    methodChannel.invokeMethod("getMeteringSessions").then((result) {
      setState(() {
        bool add = true;
        List<Session> res = [];

        for (var item in result) {
          var session = Session();
          session.id = item["id"];
          session.name = item["name"];
          session.started = item["started"];
          session.stopped = item["stopped"];
          res.add(session);

          if (session.stopped - session.started < 0 && add) {
            add = false;
          }
        }

        _canAdd = add;
        _result = res;
      });
    });
  }

  void removeSession (int index) {
    methodChannel.invokeMethod("removeMeteringSession", <String, dynamic>{
      "session": this._result[index].id
    }).then((dynamic) {
      this.loadData();
    });
  }

  void shareSession (int index) {
    methodChannel.invokeMethod("shareCsv", <String, dynamic>{
      "session": this._result[index].id
    }).then((dynamic) {
      debugPrint("${dynamic}");
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Battery Meter'),
      ),
      body: ListView.builder(
          itemCount: this._result.length,
          itemBuilder: (context, i) {
            int minutes = ((this._result[i].stopped - this._result[i].started) / 60000).floor();
            String subtitle = "${minutes} minutes";

            if (minutes < 0) {
              subtitle = "running";
            }

            return Card(
              child: ListTile(
                title: Text(
                  this._result[i].name,
                  style: const TextStyle(fontSize: 18.0),
                ),
                subtitle: Text(subtitle),
                trailing: PopupMenuButton<String>(
                  onSelected: (String result) {
                    if (result == "remove") {
                      removeSession(i);
                    } else if (result == "share") {
                      shareSession(i);
                    }
                  },
                  itemBuilder: (BuildContext context) => <PopupMenuEntry<String>>[
                    const PopupMenuItem<String>(
                      value: "remove",
                      child: Text('Remove session'),
                    ),
                    const PopupMenuItem<String>(
                      value: "share",
                      child: Text('Share session as CSV'),
                    )
                  ],
                ),
              ),
            );
          }),
      floatingActionButton: _canAdd ? FloatingActionButton(
          child: Icon(Icons.add),
          onPressed: () {
            Navigator.pushNamed(context, "/manage-session");
          }) : null
    );
  }
}
