Discontinued
============

This project will not be continued because IDEA already supports that feature. You can save and load contexts easily as shown in this screenshot:

![IDEA Context](http://ibin.co/1yLedwskhh2f "Save and load contexts in IDEA")

Tab Session
===========

Save groups of tabs and switch between different sessions.

Installation Instructions
-------------------------

**Go to the plugin settings of IntelliJ IDEA, click on `Browse repositories...`, search for "Tab Session" and install it.**

Or download the plugin JAR from the [Jetbrains Plugin: Tab Session](http://plugins.jetbrains.com/plugin?pr=&pluginId=7209) page, copy it to your `.IntelliJIdeaXX/config/plugins` folder and reload IDEA.

Or check out the source code and build the plugin manually.

What is Tab Session?
--------------------

The plugin adds two new Actions `Save Tab Session` and `Load Tab Session` to IDEA in three different menues:
* Main menu -> Windows -> Editor Tabs
* Editor Tab Menu
* Editor Context Menu

![Editor Tabs Menu](/resources/img/editor-tabs-menu.png "Editor Tabs Menu")

The save dialog allows you to save the current set of open tabs:

![Save Session Dialog](/resources/img/save-session-dialog.png "Save Session Dialog")

The load dialog allows to reload a previously saved session:

![Load Session Dialog](/resources/img/load-session-dialog.png "Load Session Dialog")

After loading a session a notification is displayed:

![Loaded Session Notification](/resources/img/loaded-notification.png "Loaded Session Notification")

Additionally, you are able to add, modify, remove and reorder sessions and their tabs in the Tab Session Settings page:

![Settings Page](/resources/img/settings-page.png "Settings Page")

If anything went wrong, you can manually change the Tab Session configuration by editing the `tabsession.xml` file in your `.idea` folder. This is not encouraged because it can lead to problems. If you are stuck, just delete the file and you start with an empty (but working) configuration.
