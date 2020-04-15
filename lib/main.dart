import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

main(){
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setSystemUIOverlayStyle(
    SystemUiOverlayStyle(
      systemNavigationBarColor: Colors.black,
      systemNavigationBarIconBrightness: Brightness.light,
      statusBarColor: Colors.black,
      statusBarIconBrightness: Brightness.light,
    )
  );
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flashlight',
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: Colors.black,
      ),
      home: Flashlight(),
    );
  }
}

class Flashlight extends StatefulWidget {
  @override
  _FlashlightState createState() => _FlashlightState();
}

class _FlashlightState extends State<Flashlight> {
  static const platform = const MethodChannel('com.az.flashlight');
  bool on = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: on ? Color(0xff242424) : Colors.black,
      body: GestureDetector(
        onTap: () async {
          if (on) {
            try {
              await platform.invokeMethod('off');
              on = false;
            } on PlatformException catch (e) {
              print('ERROR: ${e.message}');
            }
          } else {
            try {
              await platform.invokeMethod('on');
              on = true;
            } on PlatformException catch (e) {
              print('ERROR: ${e.message}');
            }
          }
          setState(() {});
        },
        child: Container(
          alignment: Alignment.center,
          decoration: BoxDecoration(
            gradient: RadialGradient(
                colors: on
                    ? [Color(0xff666666), Color(0xff333333), Colors.black]
                    : [Colors.black, Colors.black],
                center: Alignment.center),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              Expanded(
                child: Container(
                  height: 150,
                  width: 150,
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: on ? Color(0xff888888) : Color(0xff555555),
                    shape: BoxShape.circle,
                    boxShadow: on
                        ? [
                            BoxShadow(
                              color: Colors.white,
                              blurRadius: 24,
                              spreadRadius: 8,
                            )
                          ]
                        : [BoxShadow(color: Colors.black)],
                  ),
                  child: Icon(
                    Icons.power_settings_new,
                    size: 80,
                    color: on ? Colors.white : Colors.black,
                  ),
                ),
              ),
              Text(
                '--- App by Md.Azharuddin ---\n',
                style: TextStyle(
                  color: Color(0xff666666),
                  fontSize: 12,
                  fontWeight: FontWeight.w300
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
