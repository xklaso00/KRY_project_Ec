# KRY_project_Ec
if you get error with something like No such provider as "BC" make sure you have linked the bcprov-jdk15on-167.jar library in libs folder
Master Password is B3tt3rP4ssW0rd, for now you can find it in code too, but the code is used only for creating CA so it could be deleted.
Other passowrds for users are usually: hello.
You need to shomehow run 2 of these programs at once, eiter 2 jars or 1 jar and 1 IDE or some other way.
There is a jar file generated in out/artefacts... folder, it also has the needed directories, you can use that.

If you want to send encryted file, you need cert of the other side in cert directory, also on receiving end you need the other cert, also you MUST be logged in.
You can get the cert to other side by sending it as a file with Send file button (must first hit receive file in the other window).
# Run Sender and Receiver
You need to clone repository twice and open 2 IDEs in order to launch Sender and Receiver seperately otherwise they would attempt to work at the same thread.
Dont forget to launch Receiver application first.
