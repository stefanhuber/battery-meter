import 'package:flutter/material.dart';
import 'package:battery_meter/models/session.dart';

class SessionListWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => new SessionListState();
}

class SessionListState extends State<SessionListWidget> {

  List<Session> _list;
  String _selection;

  SessionListWidget(List<Session> list) {
    this._list = list;
  }

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
        itemCount: this._list.length,
        itemBuilder: (context, i) {
          return Card(
            child:ListTile(
              title: Text(
                this._list[i].name,
                style: const TextStyle(fontSize: 18.0),
              ),
              subtitle: Text("${this._list[i].getStopped().difference(this._list[i].getStarted()).inMinutes} minutes"),
              trailing: IconButton(icon: Icon(Icons.more_vert), onPressed: null),
            ),
          );
        });
  }
}

//  Icon(Icons.more_vert)