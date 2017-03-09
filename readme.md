Android Things ThingWorx Demo
=======================================

This sample shows how to use DHT11 + Arduino UNO + Raspberry Pi 3 + Thingworx Platform to connect Android Things with Thingworx.

Prepare
--------------

- Raspberry Pi 3 with Android Things
- Android Studio 2.2+
- The following individual components:
    - 1 HDMI Monitor
    - 1 DHT11 sensor
    - 1 Arduino UNO
    - 1 RPI Arduino Shield Add-on
    - jumper wires

Schematics
----------
![Schematics for Raspberry Pi 3](/DHT11_I2C/rpi3_schematics.png)

Build and install
=================
On Arduino IDE, click on the "Upload" button.

On Android Studio, click on the "Run" button.


If you have everything set up correctly:
- The monitor will show the recent temperature and humidity from now on.
- If a ThingWorx Platform project is configured (see instruction below), it will upload the sensor data to ThingWorx Platform by REST API.

ThingWorx Platform configuration (optional)
==============================================
0. Go to your project in the ThingWorx Platform
0. Under *Things* (In the *MODELING* ), click the *New* button and input the *Name* and select the *Thing Template* as GenericThing.
0. Under *Properties*, click the *Add My Property*, also input the *Name* as "Temperature", select the *Base Type* as Number and checked the "Persistent" and "Logged".
0. Do again but set the *Name* as "Humidity", and click *Save* button.
0. Under *Value Streams* (In the *DATA STORAGE* ), click the *New* button, choose the *Value Stream* and input the *Name*.
0. Under *Application Keys* (In the *SECURITY* ), click the *New* button, input the *Name* and select the *User Name Reference*. For example, I chose "Administrator"(Warning! It is unsafe).
0. Under *Things* (In the *MODELING* ), edit the thing you just created and add *Tag*.
0. Use the same way to add the same *Tag* in *Value Streams* and *Application Keys*.

After running the sample, you can check that your data in ThingWorx Platform by running the service *QueryPropertyHistory*

Note: If the build.gradle isn't be update, the app will run offline and will not send sensor data to the ThingWorx Platform.

License
-------
Copyright 2016 The Android Open Source Project, Inc.
Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at
  http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.