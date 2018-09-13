## Synopsis

This is a reusable android webview library which takes care of network settings, internet connection etc.
Also provides a JavaScript bridge with Name "Android" - to communicate with Native API

## Code Example

		public class MainActivity extends BrowserActivity {
			public MainActivity(){
				super("https://vload.in/index.html");
			}
		}

## Installation

gradle: compile 'co.immanuel:browserview:0.2'


## API Reference

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
	
## Contributors

raj@immanuel.co

## License


