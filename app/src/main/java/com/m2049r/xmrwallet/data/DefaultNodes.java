/*
 * Copyright (c) 2020 m2049r
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

package com.m2049r.xmrwallet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DefaultNodes {
    IDONTWANTTOGOTOTORONTO("idontwanttogototoronto.wow.fail:34568"),
    EUWEST2("eu-west-2.wow.xmr.pm:34568"),
    SINGAPORE("singapore.muchwow.lol:34568"),
    NYC("nyc.muchwow.lol:34568"),
    AMSTERDAM("amsterdam.muchwow.lol:34568"),
    SUCHWOW("node.suchwow.xyz:34568");

    @Getter
    private final String uri;
}
