#!/usr/bin/python
import pexpect
import sys

child = pexpect.spawn("./pack.sh")
child.logfile = sys.stdout
child.expect ("Enter Passphrase for keystore: ")
child.sendline ('tijolo22')
child.expect("final success")
