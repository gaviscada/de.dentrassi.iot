/*
 * Copyright (C) 2016 Jens Reimann <jreimann@redhat.com>
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

package de.dentrassi.iot.w1.parser;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dentrassi.iot.w1.ErrorSensorValue;
import de.dentrassi.iot.w1.Sensor;
import de.dentrassi.iot.w1.SensorValue;
import de.dentrassi.iot.w1.TemperatureValue;

public class TemperatureParser implements LineParser {
    private static final Pattern PATTERN = Pattern.compile(".*t=(\\d+)$");

    @Override
    public Optional<SensorValue> parse(final Sensor sensor, final Instant timestamp, final String line) {

        if (line.startsWith("00 00 00 00 00 00 00 00 00")) {
            return Optional.of(new ErrorSensorValue(sensor, new IllegalStateException("Sensor lost"), timestamp));
        }

        final Matcher m = PATTERN.matcher(line);
        if (!m.matches()) {
            return Optional.empty();
        }

        try {
            final String value = m.group(1);
            final double tempValue = Integer.parseInt(value) / 1000.0;
            return Optional.of(new TemperatureValue(sensor, tempValue, timestamp));
        } catch (final Exception e) {
            // something is wrong, but we detected a match
            return Optional.of(new ErrorSensorValue(sensor, e, timestamp));
        }
    }
}
