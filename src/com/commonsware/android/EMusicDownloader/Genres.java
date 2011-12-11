/*
    FOSS eMusic - a free eMusic app for Android
    This application is not associated with eMusic.com in any way.

    Copyright (C) 2010 Jack Deslippe

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package com.commonsware.android.EMusicDownloader;

class Genres extends Object {

    public final static String[] genres = {"Favorites","Alternative/Punk","Audiobooks","Blues","Classical","Country/Folk",
                              "Electronic","Hip-Hop/R&B","International","Jazz","New Age","Rock/Pop",
                              "Soundtracks/Other","Spiritual"};

    public final static String[] genreIds = {"0","284","0","292","279","286","281","282","288","291","287","277","289","290"};

    public final static String[][] genreSubcategories = {{},{"All Alternative/Punk","Alt/Punk Ska","Alternative","Alternative Experimental",
                                       "Alternative Hard Rock","Brit Pop","Emo","Garage Rock","Goth","Hardcore","Indie Pop",
                                       "Indie Rock","Industrial","Live Alt/Punk","Math Rock","New Wave","Noise Rock",
                                       "Post-Punk","Post-Rock","Psych","Punk","Rock"},
                                       {},
                                       {"All Blues","Boogie Woogie","Chicago Blues","Contemporary Blues","Delta Blues","Inspirational",
                                        "Louisiana Blues","New Orleans R&B","Soul/R&B","Texas Blues","Traditional Blues"},
                                       {"All Classical","20th Century","Avant-garde","Baroque","Chamber Music","Classical",
                                        "Classical Sacred","Classical Vocal","Guitar","Keyboard","Medieval/Renaissance","Opera",
                                        "Orchestral","Postmodern","Pre-Baroque","Romantic"},
                                       {"All Country/Folk","Alt-Country","Americana","Bluegrass","British Folk","British/Irish Folk",
                                        "Cajun/ Zydeco","Compilations","Contemporary Country","Contemporary Folk","Folk Singer-Songwriter",
                                        "Honky Tonk","Live Country/Folk","Native American Folk","Rockabilly","Traditional Country",
                                        "Traditional Folk","Western Swing"},
                                       {"All Electronic","2-Step/Garage","Abstract","Breaks","Compilations","Dance","DJ Mix","Downtempo",
                                        "Drum 'n' Bass/ Jungle","Electroclash","Electronic 12-inches","Electronic Ambient","Electronic Dub",
                                        "Electronic Experimental","Electronic Industrial","Global Groove","House","house","IDM","Jungle",
                                        "Krautrock","Leftfield","Nu-Jazz/Broken Beat","Remix","Techno","Trance","Trip-hop","Turntablism"},
                                       {"All Hip-Hop/R&B","Bass","Electro","Funk","Hip-Hop","Hip-Hop 12-inches","Live Urban/Hip-Hop","R&B",
                                        "Rap","Soul"},
                                       {"All International","Africa","Asia","Australia","Brazil","Caribbean","Celtic/British Isles","Central Asia",
                                        "Central Europe","Cuba","Dancehall","Dub","Eastern Europe","Flamenco/Tango","France","Greece","India",
                                        "Indian Classical","Indian Film","Indonesia","Italy","Latin","Mediterranean","Mexico","Middle East",
                                        "Morocco","Nordic","North America","Pacific Islands","Pakistan","Peru","Portugal","Rai","Roots Reggae",
                                        "Salsa","Ska","South America","Spain","Western Europe","World Compilations","World Fusion"},
                                       {"All Jazz","Acid Jazz","Avant-garde","Bebop","Big Band","Classic Jazz","Contemporary","Easy Listening",
                                        "Free Jazz","Fusion","Jazz Vocal","Jazz/Blues","Klezmer","Latin Jazz","Live Jazz","Lounge","Swing","Traditional"},
                                       {"All New Age","Contemporary Instrumental","Healing","New Age Ambient","New Age Environmental","New Age Sacred",
                                        "New Age Vocal","Relaxation","World"},
                                       {"All Rock/Pop","'80s","British Invasion","Classic Rock","Commercial Alternative","Death Metal","Disco",
                                        "Hard Rock","Jam Rock","Live Rock/Pop","Metal","Oldies","Pop","Power Pop","Progressive Rock","Psychedelic Rock",
                                        "Rock/Pop New Wave","Rock/Pop Rockabilly","Singer-Songwriter","Surf Rock","Synthpop","Vocal Pop"},
                                       {"All Soundtracks/Other","Children's","Comedy","Film Soundtracks","Holiday","Marching Band","Novelty",
                                        "Other","Other Environmental","Radio","Sound Effects","Spoken Word","Theatre Scores","TV Soundtracks"},
                                       {"All Spiritual","Christian","Christian Rock","Gospel","Judaica"}
                                       };

    public final static String[][] genreSubcategoryIds = {{},{"0","302","300","403","429","493","305","307","309","304","492",
                                       "301","308","500","415","506","641","373","621","306","303","293"},
                                       {},
                                       {"0","410","359","361","358","418","457","409","462","408","362"},
                                       {"0","366","487","364","369","453","488","368","507","456","452","367","454","505","363","365"},
                                       {"0","319","440","411","446","491","510","450","318","461","321","441","501","490","439","317","320","442"},
                                       {"0","479","426","315","433","401","432","424","311","480","434","310","377","316","402","482","313","660",
                                        "468","376","481","478","483","397","312","314","423","331"},
                                       {"0","427","484","327","330","437","499","328","329","326"},
                                       {"0","332","388","395","420","394","380","386","393","421","335","337","400","413","494","516","425","515",
                                        "514","496","387","391","379","381","390","370","382","389","392","384","422","385","460","338","504","336",
                                        "333","495","383","431","334"},
                                       {"0","352","354","399","356","465","349","396","640","355","357","278","419","353","503","350","351","348"},
                                       {"0","325","509","323","436","435","417","508","324"},
                                       {"0","298","416","296","374","600","297","428","404","497","295","294","285","375","485","371","372","378",
                                        "405","299","622","345"},
                                       {"0","344","343","340","398","407","620","414","412","467","406","342","341","339"},
                                       {"0","346","464","347","466"}
                                       };

}
