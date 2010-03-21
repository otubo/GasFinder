#!/usr/bin/python
# -*- coding: utf-8 -*-

import yql
import android
import time

droid = android.Android()
location = droid.getLastKnownLocation().result

#droid.startLocating()
#time.sleep(15)
#location = droid.readLocation().result

longitude = location['longitude']
latitude = location['latitude']

print latitude
print longitude

URL = "USE 'http://www.meusgastos.com.br/odt/meuspostos.precos.coordenadas.xml' as meuspostos.precos.coordenadas; SELECT * FROM meuspostos.precos.coordenadas WHERE lat=%.4f and lon=%.4f" % (latitude,longitude)

y = yql.Public()
res = y.execute(URL)

for row in res['query']['results']['item']:
    print row['nome']

#res.rows
