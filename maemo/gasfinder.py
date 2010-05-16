#!/usr/bin/python
# -*- coding: utf-8 -*-

import yql
import gtk
import time
import location
import gobject

gas_station_name = []
gas_station_dic = {}
latlong = 0

def find_gas():
    i = 0
    URL = "USE 'http://www.meusgastos.com.br/odt/meuspostos.precos.coordenadas.xml' as meuspostos.precos.coordenadas; SELECT * FROM meuspostos.precos.coordenadas WHERE lat=%f and lon=%f and sort=1" % device.fix[4:6]
    y = yql.Public()
    full_result = y.execute(URL)
    for row in full_result['query']['results']['item']:
        name_and_price = "%s (R$%s)" % (row['nome'],row['gasolina'])
        gas_station_name.append(name_and_price)
        gas_station_dic[i] = row['nome']
        i += 1

def main():
     find_gas()
     window = gtk.Window(gtk.WINDOW_TOPLEVEL)
     
     label = gtk.Label("Hello World!")
     window.add(label)
     
     label.show()
     window.show()
     
     gtk.main() 

def on_error(control, error, data):
    #print "location error: %d... quitting" % error
    data.quit()

def on_changed(device, data):
    if not device:
        return

    if device.fix:
        if device.fix[1] & location.GPS_DEVICE_LATLONG_SET:
            latlong = device.fix[4:6]
            #print "lat = %f, long = %f" % device.fix[4:6]
            data.stop()

def on_stop(control, data):
    #print "quitting"
    #data.quit()
    main()

def start_location(data):
    data.start()
    return False

loop = gobject.MainLoop()

control = location.GPSDControl.get_default()
device = location.GPSDevice()
control.set_properties(preferred_method=location.METHOD_USER_SELECTED,
preferred_interval=location.INTERVAL_DEFAULT)

control.connect("error-verbose", on_error, loop)
device.connect("changed", on_changed, control)
control.connect("gpsd-stopped", on_stop, loop)

gobject.idle_add(start_location, control)

loop.run()
