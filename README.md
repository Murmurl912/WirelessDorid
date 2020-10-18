# HttpServer

This is a part of long term project: android wirless drive.
The main objective is using wifie adapter existing in android device to serve
as a wirless drive, hence using as little as possible of existing relay servers as possible.
Thus user's privarcy and security can be protected.

There are at lest two problems to sovle to achieve a wilress drive. First is how to connect android deivce that act as a file
server to client deives which want to access it. Second is how to manage file over network. In this test project, a
http server is used to sharing files over network, ftp and other can also used in android system.

As for connecting android device with other devices, there are many way to achieve it. One way is using local network, connect android device 
with other deivce in the same network. Hance this can be achieve using wifi hotspot or a wifi router.
In this way user must connect device again whenever network address in android device is changed.
Using qr code, bluetooth or nfc change easy the network address and other necessary info exchanging steps.

Another way to do this is using nat hole punching, the representive project is sycthing, a distruted 
file synchorning system. However the disadvantages is that a client must be installed in both devices who want
to exchange data. And currently the project only supports file synchorning rather than serve as a network file system.
It may can be adopt to a file system.

Another way is using wifi direct, which is support by most of computer, phone and other devices. There has been
example deivces such as printers, wirless camera using this technology to connect with a client deivce. However no
wifi direct file sharing application has been developped to achive share file cross os, such as windows, linux and android.
