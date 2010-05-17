#!/usr/bin/python
# -*- coding: utf-8 -*-

import yql
import gtk
import hildon
import time
import location
import gobject

latlong = 0
gas_station_list = []

def selection_changed(selector, user_data):
    current_selection = selector.get_current_text()
    # Create the main window
    win = hildon.StackableWindow()
    win.set_title(current_selection)

    for row in full_result['query']['results']['item']:
        name_and_price = "%s (R$%s)" % (row['nome'],row['gasolina'])
        if name_and_price == current_selection:
            textarea = gtk.TextView()
            textarea.set_editable(False)
            buffer = textarea.get_buffer()
            iter = buffer.get_iter_at_mark(buffer.get_insert())
            buffer.insert(iter,"%s\n" % row['nome'])
            buffer.insert(iter,"%s, %s\n" % (row['endereco'], row['bairro']))
            buffer.insert(iter,"Gasolina: R$%s\n" % row['gasolina'])
            buffer.insert(iter,"Alcool: R$%s\n" % row['alcool'])
            buffer.insert(iter,"Diesel: R$%s\n" % row['diesel'])
            buffer.insert(iter,"GNV: R$%s\n" % row['gnv'])

            vbox = gtk.VBox(False, 0)
            vbox.pack_start(textarea, True, True, 0)
   
            win.add(vbox)
            win.show_all()

def create_simple_selector():
    #Create a HildonTouchSelector with a single text column
    selector = hildon.TouchSelector(text = True)
 
    # Set a handler to "changed" signal
    selector.connect("changed", selection_changed)

    for name_and_price in gas_station_list:
        selector.append_text(name_and_price)
 
    # Set selection mode to allow multiple selection
    selector.set_column_selection_mode(hildon.TOUCH_SELECTOR_SELECTION_MODE_SINGLE)
 
    return selector

def app_quit(widget, data=None):
    gtk.main_quit()

def main():
    program = hildon.Program.get_instance()
    gtk.set_application_name("hildon-touch-selector example program")
 
    window = hildon.StackableWindow()
    program.add_window(window)
 
    # Create touch selector
    selector = create_simple_selector()
    window.add(selector)
 
    window.connect("destroy", app_quit)
 
    window.show_all()
 
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

i=0
URL = "USE 'http://www.meusgastos.com.br/odt/meuspostos.precos.coordenadas.xml' as meuspostos.precos.coordenadas; SELECT * FROM meuspostos.precos.coordenadas WHERE lat=%f and lon=%f and sort=1" % device.fix[4:6]
y = yql.Public()
full_result = y.execute(URL)
for row in full_result['query']['results']['item']:
    name_and_price = "%s (R$%s)" % (row['nome'],row['gasolina'])
    gas_station_list.append(name_and_price)

control.connect("gpsd-stopped", on_stop, loop)

gobject.idle_add(start_location, control)

loop.run()
