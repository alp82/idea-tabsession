idea-tabsession
===============

IntelliJ IDEA plugin to save groups of tabs and switch between different sessions.

The plugin adds two new Actions `Save Tab Session` and `Load Tab Session` to IDEA in three different menues:
* Main menu -> Windows -> Editor Tabs
* Editor Tab Menu
* Editor Context Menu

![Editor Tabs Menu](/resources/img/editor-tabs-menu.jpg "Editor Tabs Menu")

The save dialog allows you to save the current set of open tabs:
![Save Session Dialog](/resources/img/save-session-dialog.jpg "Save Session Dialog")

The load dialog allows to reload a previously saved session:
![Load Session Dialog](/resources/img/load-session-dialog.jpg "Load Session Dialog")

After loading a session a notification is displayed:
![Loaded Session Notification](/resources/img/loaded-notification.jpg "Loaded Session Notification")

In a future version it will be possible to manage existing sessions in order to modify or remove them. To manually do that, just edit the `tabsession.xml` configuration file in your `.idea` folder.