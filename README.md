# PixelWatchFace
A minimalistic and open-source watchface for WearOS

# TODO
## Watch App
- Replace icons with the icons from here: 
- Correspond all of those icons with Dark Sky weather codes
- Try and match as many OpenStreetMap weather codes to icons as possible
- Add summary for Dark Sky users that will show the "summary" from dark sky on the watch face (when retrieving it use the user's language code on their watch to get the right language)
- Add an "At a glance" style widget or complication that shows calendar data
## Phone App
- Add setting to let the user pick how often the watch checks for weather (30 min at the least, 6 hours at the most or similar)
- Add setting to toggle the Dark Sky summary
- Add setting to toggle "At a glance" stuff
- Create an actual UI for the companion app that looks nice for the settings (maybe show a rough preview of the watch screen at the top and then update that when you change settings)
- Add subtle explanations that explain what extra features using the Dark Sky API gives you
- Add yearly subscription that covers the cost of Dark Sky API calls for a year 
If the watch checked the Dark Sky weather API every 30 minutes for 365 days it would cost $1.752 USD. Google takes a 30% cut so 1.752 = x - .3x.  x = $2.503 USD as the minimum yearly subscription cost. If I make that a simple $3/year Google would take $0.9 leaving $2.1 left over. The API costs would take $1.752 from that meaning I would make $0.348 per year per person. That is alright I guess.
- Add donation options for supporting me and my projects
## Both
- Add crowdin localization to the phone app, play store listing, and watch app where possible
