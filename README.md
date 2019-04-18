## Synopsis

This is a reusable android webview library which takes care of network settings, internet connection etc.
Also provides a JavaScript bridge with Name "Android" - to communicate with Native API

https://travis-ci.org/immi5556/Android-BrowserView.svg?branch=master

## Code Example

		public class MainActivity extends BrowserActivity {
			public MainActivity(){
				super("https://vload.in/index.html");
			}
		}

## Installation

gradle: compile 'co.immanuel:browserview:0.2'


## API Reference

```
1. Push Notification:
<script>
	var fnpush = function (msg) {
            alert(msg);
        }
	Android.enablePushNotification("fnpush"); //Pass Name of the function as callback
</script>	

2. DeviceToken
	Android.getDeviceToken();
	
3. DeviceId
	Android.getDeviceId();	

4. DeviceType
	Android.getDeviceType();

5. OsVersion
	Android.getOsVersion();
	
6. DeviceModel
	Android.getDeviceModel();	
	
7. DeviceManufacturer
	Android.getDeviceManufacturer();	
	
8. Get GPS Location	
	<script>
		Android.enableLocation("getloc", "freqloc"); // First Param, Callback Function - Send the Location Status, Second Param - Frequency Callback Function for Every 1 minute or Location changes to 10M
		var getloc = function (msg) {
		    alert(msg);
		}

		var freqloc = function (msg) {
		    alert(msg);
		}
	</script>
```	
	
## Contributors

raj@immanuel.co

## License


