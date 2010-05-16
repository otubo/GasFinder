#!/usr/bin/python
# -*- coding: utf-8 -*-

import yql
import android
import time

gas_station_name = []
gas_station_dic = {}
i = 0
droid = android.Android()

droid.startLocating()
#time.sleep(15)
location = droid.readLocation().result

#XXX: must find a way to check if the GPS returned
#     or not.
location = droid.getLastKnownLocation().result

longitude = location['longitude']
latitude = location['latitude']

URL = "USE 'http://www.meusgastos.com.br/odt/meuspostos.precos.coordenadas.xml' as meuspostos.precos.coordenadas; SELECT * FROM meuspostos.precos.coordenadas WHERE lat=%.4f and lon=%.4f and sort=1" % (latitude,longitude)

y = yql.Public()
full_result = y.execute(URL)

for row in full_result['query']['results']['item']:
    name_and_price = "%s (R$%s)" % (row['nome'],row['gasolina'])
    gas_station_name.append(name_and_price)
    gas_station_dic[i] = row['nome']
    i += 1

droid.dialogCreateAlert("Gas Stations near you")
droid.dialogSetItems(gas_station_name)
droid.dialogShow()
user_choice = droid.dialogGetResponse().result

#XXX: And this is the worst way to find a record. Please fix me
for row in full_result['query']['results']['item']:
    if row['nome'] == gas_station_dic[user_choice['item']]:
        gas_station_coordinate = "%s,%s" % (row['latitude'],row['longitude'])
        droid.map(gas_station_coordinate)

