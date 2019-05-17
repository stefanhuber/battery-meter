class Session {

  int id;
  String name;
  int started;
  int stopped;

  DateTime getStarted() {
    return DateTime.fromMicrosecondsSinceEpoch(started * 1000);
  }

  DateTime getStopped() {
    return DateTime.fromMicrosecondsSinceEpoch(stopped * 1000);
  }

}