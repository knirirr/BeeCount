## Information

This is the second version of BeeCount. The older, "classic" version can be found
in [this repository](https://code.google.com/p/beecount/). This newer version has been
considerably re-written so that the underlying data management and UI code is much easier to
maintain and extend, and the UI has been modernised.

More information can be found on the [BeeCount home page](http://knirirr.com/beecount).

## How to get BeeCount

BeeCount is no longer available from the Play Store due to Google's policies and might not
be installable in 2027 for the same reason(I won't upload my official ID in order to keep 
distributing FOSS apps). BeeCountwill remain accessible via this Github repo. Obtanium will 
allow you to keep it updated:

https://obtainium.imranr.dev/

Give Obtanium this URL as the source: https://github.com/knirirr/BeeCount

The version available here via Obtanium is signed with the same certificate as the old Google Play 
version, so if you're coming from that then other than the minor inconvenience of setting up 
Obtanium then there should be no issues. 

Or, you can get BeeCount via Fdroid:

<a href="https://f-droid.org/packages/com.knirirr.beecount/" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80"/></a>

This is signed with Fdroid's certificate so if you switch to this source from Google Play you'll 
need to back up your database, remove BeeCount and reinstall from Fdroid, then restore your data.
Some users are reporting issues with restoring data (I can't pin down which devices are affected) 
so it's safer to use Obtanium. 

## Permissions

Permission is requested to manage all files so that you can import beecount.db from your Download
folder. BeeCount doesn't access any other files.

To enable this, open the Permission Manager in your phone's settings and look for apps which
are able to manage files (you may need to tap on a link to "see more apps that can access all 
files"). 

## Help Wanted

BeeCount has been slightly re-written recently. The main change you'll notice is that the background
image has disappeared and the colour scheme has changed. This is to make maintenance easier as well
as to bring the components up-to-date.

Assistance is always welcome as I don't usually have much time free to devote to BeeCount work. 
Anyone who's willing to look into issues with dumping/restoring the database would be most welcome;
 it works for me but some users report problems and it's not clear which versions of Android are
affected. 

## Licence etc.

Copyright 2018 Milo Thurston

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

