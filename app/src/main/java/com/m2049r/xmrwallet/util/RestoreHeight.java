/*
 * Copyright (c) 2018 m2049r
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.m2049r.xmrwallet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class RestoreHeight {
    static final int DIFFICULTY_TARGET = 120; // seconds

    static private RestoreHeight Singleton = null;

    static final int DIFFICULTY_TARGET = 300; // seconds

    static public RestoreHeight getInstance() {
        if (Singleton == null) {
            synchronized (RestoreHeight.class) {
                if (Singleton == null) {
                    Singleton = new RestoreHeight();
                }
            }
        }
        return Singleton;
    }

    private Map<String, Long> blockheight = new HashMap<>();

    RestoreHeight() {
        blockheight.put("2018-04-01", 1L);
        blockheight.put("2018-05-01", 8667L);
        blockheight.put("2018-06-01", 17461L);
        blockheight.put("2018-07-01", 25918L);
        blockheight.put("2018-08-01", 34723L);
        blockheight.put("2018-09-01", 43573L);
        blockheight.put("2018-10-01", 52103L);
        blockheight.put("2018-11-01", 60701L);
        blockheight.put("2018-12-01", 66773L);
        blockheight.put("2019-01-01", 72734L);
        blockheight.put("2019-02-01", 78146L);
        blockheight.put("2019-03-01", 84733L);
        blockheight.put("2019-04-01", 93590L);
        blockheight.put("2019-05-01", 102219L);
        blockheight.put("2019-06-01", 111126L);
        blockheight.put("2019-07-01", 119851L);
        blockheight.put("2019-08-01", 128717L);
        blockheight.put("2019-09-01", 137696L);
        blockheight.put("2019-10-01", 146308L);
        blockheight.put("2019-11-01", 155269L);
        blockheight.put("2019-12-01", 163856L);
        blockheight.put("2020-01-01", 172781L);
        blockheight.put("2020-02-01", 181722L);
        blockheight.put("2020-03-01", 190060L);
        blockheight.put("2020-04-01", 199009L);
        blockheight.put("2020-05-01", 207571L);
        blockheight.put("2020-06-01", 216314L);
        blockheight.put("2020-07-01", 224889L);
        blockheight.put("2020-08-01", 233808L);
        blockheight.put("2020-09-01", 242712L);
        blockheight.put("2020-10-01", 251334L);
        blockheight.put("2020-11-01", 260297L);
        blockheight.put("2020-12-01", 268982L);
        blockheight.put("2021-01-01", 277930L);
        blockheight.put("2021-02-01", 286873L);
        blockheight.put("2021-03-01", 294950L);
        blockheight.put("2021-04-01", 303887L);
    }

    public long getHeight(String date) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        parser.setLenient(false);
        try {
            return getHeight(parser.parse(date));
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public long getHeight(final Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.DST_OFFSET, 0);
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -4); // give it some leeway
        if (cal.get(Calendar.YEAR) < 2018)
            return 0;
        if ((cal.get(Calendar.YEAR) == 2018) && (cal.get(Calendar.MONTH) < 3))
            // before April 2018
            return 0;

        Calendar query = (Calendar) cal.clone();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        String queryDate = formatter.format(date);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        long prevTime = cal.getTimeInMillis();
        String prevDate = formatter.format(prevTime);
        // lookup blockheight at first of the month
        Long prevBc = blockheight.get(prevDate);
        if (prevBc == null) {
            // if too recent, go back in time and find latest one we have
            while (prevBc == null) {
                cal.add(Calendar.MONTH, -1);
                if (cal.get(Calendar.YEAR) < 2014) {
                    throw new IllegalStateException("endless loop looking for blockheight");
                }
                prevTime = cal.getTimeInMillis();
                prevDate = formatter.format(prevTime);
                prevBc = blockheight.get(prevDate);
            }
        }
        long height = prevBc;
        // now we have a blockheight & a date ON or BEFORE the restore date requested
        if (queryDate.equals(prevDate)) return height;
        // see if we have a blockheight after this date
        cal.add(Calendar.MONTH, 1);
        long nextTime = cal.getTimeInMillis();
        String nextDate = formatter.format(nextTime);
        Long nextBc = blockheight.get(nextDate);
        if (nextBc != null) { // we have a range - interpolate the blockheight we are looking for
            long diff = nextBc - prevBc;
            long diffDays = TimeUnit.DAYS.convert(nextTime - prevTime, TimeUnit.MILLISECONDS);
            long days = TimeUnit.DAYS.convert(query.getTimeInMillis() - prevTime,
                    TimeUnit.MILLISECONDS);
            height = Math.round(prevBc + diff * (1.0 * days / diffDays));
        } else {
            long days = TimeUnit.DAYS.convert(query.getTimeInMillis() - prevTime,
                    TimeUnit.MILLISECONDS);
            height = Math.round(prevBc + 1.0 * days * (24f * 60 * 60 / DIFFICULTY_TARGET));
        }
        return height;
    }
}
