# PixelWatchFace
A minimalistic and open-source watchface for WearOS


# TODO
## Watch App
Current planned features:
- New and more icons to match all of the different weather types available from Dark Sky
- Built-in $3ish subscription if you don't want to use a Dark Sky API key and/or want to support the development of the app
- Option to show the Dark Sky summary at the bottom of the watch face
- Option to show calendar events similar to the "At a glance" widget on pixel devices
- Option to adjust weather update interval (currently defaults to 30 min)

Edit (features recommended by you guys):
- Move time down a bit to be more centered
- Option to display watch battery
- Add methods to support development
- Option to put complications beneath the weather readout
- Option to toggle display of WearOS logo
- Option to display heartbeat
- Show upcoming alarms
- Replace icons with the icons from here: 
- Correspond all of those icons with Dark Sky weather codes
- Try and match as many OpenStreetMap weather codes to icons as possible
- Add summary for Dark Sky users that will show the "summary" from dark sky on the watch face
- Add an "At a glance" style widget or complication that shows calendar data
### Done
- Option to enable an ambient mode with white outlined text
- Add more spacing between clock and time, increase sizes if possible
- Enable burn in protection for ambient mode
- Add comma after day to match Pixel phones
- Retrieve the Dark Sky summary in the user's language
- Option for European date style (Thu 14 Feb)
- Option to remove the temperature decimal point
## Phone App
- Add setting to let the user pick how often the watch checks for weather (30 min at the least, 6 hours at the most or similar)
- Add setting to toggle the Dark Sky summary
- Add setting to toggle "At a glance" stuff
- Create an actual UI for the companion app that looks nice for the settings (maybe show a rough preview of the watch screen at the top and then update that when you change settings)
- Displaying preview of watch face:
  - Have the watch send its watchbounds in the oncreate method
  - Have the phone check for this info and use it in shaping the preview window
  - Sync settings from the watch vs sharedpreferences on the phone
  

- Add subtle explanations that explain what extra features using the Dark Sky API gives you
- Add yearly subscription that covers the cost of Dark Sky API calls for a year 
If the watch checked the Dark Sky weather API every 30 minutes for 365 days it would cost $1.752 USD. Google takes a 30% cut so 1.752 = x - .3x.  x = $2.503 USD as the minimum yearly subscription cost. If I make that a simple $3/year Google would take $0.9 leaving $2.1 left over. The API costs would take $1.752 from that meaning I would make $0.348 per year per person. That is alright I guess.
- Add donation options for supporting me and my projects
## Both
- Add crowdin localization to the phone app, play store listing, and watch app where possible
- Add weather.gov as a source for US users (https://api.weather.gov/points/38.0301,-84.4988/forecast for example)

Play Store link: https://play.google.com/store/apps/details?id=com.corvettecole.pixelwatchface

