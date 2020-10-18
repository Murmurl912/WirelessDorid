# Http Server

    This is a part of long term project: android wireless drive.
    The main objective is using Wi-Fi adapter existing in android devices to serve
    as a wireless drive, hence using as little as possible of existing relay servers as possible.
    Thus, user's privacy and security can be maximized.

## Objectives

1. Share android storage over networks.  
2. Share storage wireless.
3. Share storage securely.
4. Minimal configuration setup.
4. User friendly.

## Approaches

    There are at lest two problems to solve to achieve a wireless drive. First is how to connect android deivce that act as a file
    server to client devices which want to access it. Second is how to manage files over network. In this test project, a
    http server is used to sharing files over network, ftp and other can also be used in android system.

### 1. Using Local Area Network

    As for connecting android device with other devices, there are many ways to achieve it. One way is using local network, connect android device 
    with other devices in the same network. Hence, this can be achieved using Wi-Fi hotspot or a Wi-Fi router.
    In this way user must connect devices again whenever network address in a android device changed.
    Using qr code, bluetooth or nfc change easy the network address and other necessary info exchanging steps.

### 2. Using NAT Hole Punching

    Another way to do this is using nat hole punching, the representative project is sycthing, a distributed 
    file synchronization system. However, the disadvantages is that a client must be installed in both devices who want
    to exchange data. And currently the project only supports file synchronization rather than serve as a network file system.
    It may be adapted to a file system.

### 3. Using Wi-Fi Direct

    Another way is using wifi direct, which is support by most of the computers, phones and other devices. There has been
    example devices such as printers, wireless camera using this technology to connect with a client device. However, no
    Wi-Fi direct file sharing application has been developed to archive share file crossing os, such as windows, linux and android.
