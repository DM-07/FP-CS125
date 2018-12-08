package com.example.android.whatevertrash;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.SearchView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    source[] newsstream;
    RecyclerView newsrecycleview;
    newsadaptor newsadaptor;
    RecyclerView.LayoutManager newslayoutmanager;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView locationnow = findViewById(R.id.locationnow);
        final TextView description = findViewById(R.id.locationdescription);
        description.setMovementMethod(new ScrollingMovementMethod());
        final CardView descriptioncard = findViewById(R.id.descriptioncard);
        final TextView locationname = findViewById(R.id.locationname);
        final FloatingActionButton b = findViewById(R.id.floatingActionButton);
        final NotificationManagerCompat notificationmanager = NotificationManagerCompat.from(this);
        final Toast toast = Toast.makeText(this, "Getting Your Location data now......", Toast.LENGTH_LONG);

        createNotificationChannel();

        drawerLayout = findViewById(R.id.appdrawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.appNavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        if (locationnow.getText() == null || locationnow.getText().length() == 0) {
            toast.show();
        }

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationnow.setText("Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                for (int i = 0; i < newsstream.length; i++) {
                    location.distanceBetween(location.getLatitude(), location.getLongitude(), newsstream[i].latitude, newsstream[i].longitude, newsstream[i].distance);
                    for (int j = i; j >= 1; j--) {
                        if (newsstream[j].distance[0] < newsstream[j - 1].distance[0]) {
                            source temp = newsstream[j];
                            newsstream[j] = newsstream[j - 1];
                            newsstream[j - 1] = temp;
                        }
                    }
                }
                newsadaptor.update(newsstream);
                if (newsstream[0].distance[0] < 70 && !locationname.getText().equals(newsstream[0].title)) {
                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    if (newsstream[0].alreadyhere == false) {
                        newsstream[0].alreadyhere = true;
                        newsadaptor.update(newsstream);
                    }
                    descriptioncard.setVisibility(View.VISIBLE);
                    ObjectAnimator fadeanim = ObjectAnimator.ofFloat(descriptioncard, View.ALPHA, 1.0f, 0.0f);
                    fadeanim.setDuration(1000);
                    AnimatorSet fadeset = new AnimatorSet();
                    fadeset.playSequentially(fadeanim);
                    fadeset.start();
                    locationname.setText(newsstream[0].title);
                    description.setText(newsstream[0].description);
                    fadeset.reverse();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraintent.resolveActivity(getPackageManager()) != null) {
                    File picturesaveroute = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String picturename = "WhateverTrash" + locationname.getText() + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
                    File picturefile = new File(picturesaveroute, picturename);
                    if (picturefile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "WhateverTrash.provider",
                                picturefile);
                        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraintent, 1);
                    }
                }
            }
        });



        preparedata();
        newsrecycleview = findViewById(R.id.newsrecycleview);
        newsadaptor = new newsadaptor(newsstream);
        newslayoutmanager = new LinearLayoutManager(getBaseContext());
        newsrecycleview.setAdapter(newsadaptor);
        newsrecycleview.setLayoutManager(newslayoutmanager);


        final Notification refreshfailBuilder = new NotificationCompat.Builder(this, "CHANNEL_ID_1")
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setContentTitle("Sorry")
                .setContentText("An Error Occured, Please refresh")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        newsadaptor.update(newsstream);
        final source[] streamcopy = new source[newsstream.length];
        for (int i = 0; i < newsstream.length; i++) {
            streamcopy[i] = newsstream[i];
        }
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        MenuItem searchitem = menu.findItem(R.id.appsearch);
        SearchView searchView = (SearchView) searchitem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    newsstream = streamcopy;
                    newsadaptor.update(newsstream);
                }
                source[] searchlist = new source[0];
                for (source i: streamcopy) {
                    if (i.title.toLowerCase().contains(newText.toLowerCase())) {
                        source[] temp = new source[searchlist.length + 1];
                        for (int j = 0; j < searchlist.length; j++) {
                            temp[j] = searchlist[j];
                        }
                        temp[temp.length - 1] = i;
                        searchlist = temp;
                    }
                }
                if (searchlist.length > 0) {
                    newsstream = searchlist;
                    newsadaptor.update(newsstream);
                } else {
                    Toast searchtoast = Toast.makeText(getApplicationContext(), "No Search Result, Please try again.", Toast.LENGTH_LONG);
                    searchtoast.show();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawerabout) {
            Intent aboutintent = new Intent(this, aboutpage.class);
            startActivity(aboutintent);
        }

        DrawerLayout drawerLayout = findViewById(R.id.appdrawer);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelid = "CHANNEL_ID_1";
            String channelname = "channel_1";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelid, channelname, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("default channel for notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void preparedata() {
        newsstream = new source[35];
        newsstream[0] = new source("Admissions & Records Building", "Your first step at Illinois might be meeting with an admissions counselor or student representative to ask about the admission process, requirements, or just general campus and academic life. The Office of Undergraduate Admissions is the place to ask any and all preliminary questions. It’s open Monday through Friday from 8:30 a.m. to 5 p.m.",
                40.10834545094693, -88.22065265887898, false);
        newsstream[1] = new source("Illinois Street Residence Halls", "Townsend and Wardall make up the Illinois Street Residence Halls (ISR). Both are popular choices for students wanting to live close to Campustown and the Engineering Quad. In fact, a living-learning community for innovative and entrepreneurial spirits is located in Wardall. Want to know more? Check out the other university-owned residence halls on campus.",
                40.109253284287796, -88.22136541479682, false);
        newsstream[2] = new source("Krannert Center for the Performing Arts", "Champaign-Urbana is host to a variety of state-of-the-art facilities, including Krannert Center for the Performing Arts. Taking up a full city block on the Illinois campus, it’s home to more than 350 performances each year and 180 productions. These performances include everything from operas to world-renowned guitar festivals, and you’re offered a student discount to each one.",
                40.10798550725429, -88.22293987148856, false);
        newsstream[3] = new source("Burrill & Morrill Halls", "Connected via the skywalk, Burrill and Morrill Halls are home to the advising departments of the Life Sciences. Note the historical marker near the entrance to Burrill Hall. Read the bronze plaque or check out a video clip about this campus scientific advancement.",
                40.10898597498329, -88.22473695152854, false);
        newsstream[4] = new source("Natural History Building & Noyes Laboratory of Chemistry", "Heading west toward the Quad, the Natural History Building is on your right, toward the north. This is home to the Department of Geology and many of the classrooms and labs for the life sciences, including Integrative and Molecular and Cellular Biology. It’s separated from Noyes Lab by the oldest bus stop on campus.\n" +
                "\n" +
                "Noyes Laboratory of Chemistry is on your left, toward the south, and is home to the School of Chemical Sciences, composed of the departments of Physical, Organic, and Analytical Chemistry. It was once the largest building in the country devoted solely to chemical research.",
                40.10841157829214, -88.22612097138023, false);
        newsstream[5] = new source("The Quad", "From relaxing on the lawn to meeting group project members, the Quad is the central hub on campus. Every year kicks off with Quad Day, an event for all registered student organizations to gather in one place to recruit new members. Between the Illini Union on the north end and Foellinger Auditorium on the south, many of the most notable landmarks on campus can be found. You’re sure to see something different happening every day!\n" +
                "\n" + "Have you noticed any squirrels on your tour yet? It seems we have former University President Andrew S. Draper to thank for the surplus of these furry creatures. In 1901, Draper asked the university’s Professor of Geology Charles W. Rolfe to look into bringing squirrels to campus to better his students’ university experience. We hope they make your day, too!",
                40.10791923441056, -88.22721809981562, false);
        newsstream[6] = new source("Chemistry Annex", "As you head south, the Chemistry Annex will be on your left. The Chemistry Annex houses the Chemistry Learning Center, a Chemistry Help Room and computer lab that has tutors available throughout the day to assist students with homework. Computers here allow you to simulate labs online before attempting them in the classroom.",
                40.10775723632932, -88.22608971770359, false);
        newsstream[7] = new source("Davenport Hall", "Davenport Hall is home to the departments of Anthropology and Geography. When the building was home to the College of Agriculture (hence the title on the front of the building), the first level was used as a stock pavilion—check out the Illini Super Sweet Corn Historical Marker near the entrance. Don’t worry, though; the smell has since been relocated to the South Farms!",
                40.107191035111555, -88.2262184637363, false);
        newsstream[8] = new source("Foreign Language Building", "Built in 1971, the Foreign Language Building is the youngest building on the Quad. You have your choice of 33 languages other than English and 8 language departments. The building also houses the departments of Theology, Linguistics, and Classical Civilizations. Notice the unique structure? The building was originally constructed to collapse away from its center after housing a super computer during the Cold War era.",
                40.10626787084597, -88.226164819556, false);
        newsstream[9] = new source("Foellinger Auditorium", "Foellinger Auditorium, completed in 1907, resembles Thomas Jefferson's rotunda on the University of Virginia campus. It’s home to the largest lectures at Illinois. But it’s not just for academics. Foellinger is also home to major campus lectures and performances, including past guests Ralph Nader, Patch Adams, Bill Gates, Sara Bareilles, Sean Austin, and the Mythbusters.\n" +
                "\n" + "Foellinger Auditorium has quite a few secrets to discover. For example, did you know that our Quad Cam is positioned on top of the building? What’s more, if you walk onto the lower patio of Foellinger, position yourself on the round medallion, face the building, and whisper, it should echo back to you.",
                40.105935528643435, -88.22716796572757, false);
        newsstream[10] = new source("Smith Memorial Hall", "This building is one of the homes of the School of Music. There are 77 pianos on the top floor (roughly 20 tons of weight!), along with 49 student practice rooms. If you play the piano, make sure to stop in and play a romantic piece for Captain Thomas J. Smith. He dedicated this Beaux-Arts architecture-style building to the memory of the love his life, Tina Weedon Smith, with the hope that it would \"in a measure bless mankind.\"",
                40.10568114215682, -88.22604143794132, false);
        newsstream[11] = new source("Undergraduate Library & Morrow Plots", "Have you made it to the brick patio yet? Rest assured, you haven’t missed the Undergraduate Library—it’s just beneath you! One of the most popular places on campus, the library is open until 2:30 a.m. most days. Take a virtual tour of the Undergraduate Library to check it out from home.\n" +
                "\n" +
                "Why’s it 2 stories underground, you ask? To avoid casting a shadow on the Morrow Plots, the cornfield you see further off to the east. The longest-running experimental cornfield in the Western Hemisphere, the Morrow Plots was named a National Historical Landmark in 1968.\n" +
                "\n" + "The only thing better than a cornfield is a great song dedicated to our cornfield. Our premier a cappella group on campus, The Other Guys, perform a Morrow Plot song. If you have a singing talent, play an instrument, compete in a sport, like to jump out of planes, enjoy the month of October, or just like to eat barbeque (the Grillini!) we have over 1,000 student organizations waiting for you!",
                40.10470559147577, -88.22717785835266, false);
        newsstream[12] = new source("Main Library", "The Main Library is the building west of the Undergraduate Library. The university houses more than 14 million volumes, including a copy of the Gutenberg Bible, the first major book printed with a movable type printing press. In total, we have over 20 libraries, creating the largest public academic library system in the world.\n" +
                "\n" +
                "Interested in a challenge? Enter the Main Library and find the underground tunnel that connects with the Undergraduate Library. This is where many students choose to study. There's also a coffee shop located here.",
                40.10467306282453, -88.22866236430963, false);
        newsstream[13] = new source("Gregory Hall", "Gregory Hall was named after the first president of the University of Illinois, John Milton Gregory, who’s actually buried outside Altgeld Hall (a building you'll see later on this tour). Gregory Hall houses the departments of History and Philosophy and the College of Media.\n" +
                "\n" + "Roger Ebert, late film critic and alumnus of the College of Media, attended many of his classes in Gregory Hall. Each year, the College of Media hosts his famed Ebertfest Film Festival in conjunction with the Virginia Theatre.",
                40.105641381944864, -88.22812055808862, false);
        newsstream[14] = new source("Lincoln Hall", "Lincoln Hall is the recently renovated building originally built for the anniversary of Lincoln’s birthday. In fact, it reopened as one of the nation's \"greenest\" and \"most wired\" classroom buildings. Rumor has it that rubbing Lincoln’s nose before an exam brings good luck. The building now holds the College of Liberal Arts and Sciences administrative offices, as well as the departments of Communication, Political Science, and Sociology.",
                40.10656455471243, -88.22825466853936, false);
        newsstream[15] = new source("The Eternal Flame", "The pillar you see between Lincoln Hall and the English Building is the Eternal Flame, a gift from the graduating class of 1912. Formerly an ever-burning oil lamp, the flame promised eternal love after kissing your sweetheart beneath it: \"A lover’s kiss will bring eternal bliss.\" Now the electric light tends to flicker on and off, so students have changed the myth: Lovers who kiss underneath the flame are doomed to an on-again, off-again relationship. Kiss your special someone here and find out!",
                40.10708562892087, -88.22779869300683, false);
        newsstream[16] = new source("English Building", "The English Building is home to the departments of English, Rhetoric, and Business and Technical Writing. Originally, it was the Women's Building—a self-contained unit with classrooms, dormitories, and a pool in the basement. Legend has it that a woman drowned in the 1940s, and her ghost still haunts the English Building to this day. We'll meet you in the English Building after dark this evening.",
                40.10756472581236, -88.22814875676193, false);
        newsstream[17] = new source("Henry Administrative Building", "Upon first glance, you might notice that the ground floor of the Henry Administration Building resembles the Empire State Building. In fact, the Empire State Building was created over 20 years later! The Henry Administration Building contains many administrative departments and offices, as well as various discussion classrooms.",
                40.10836133249891, -88.22821319103241, false);
        newsstream[18] = new source("Altgeld Hall", "Altgeld Hall, now home to the Math and Actuarial Sciences departments, was once home to the pink limestone library and the College of Law. Altgeld also stands as the original bell tower on campus. Automated chimes ring every 15 minutes, in addition to a special lunchtime concert. Due to the size of the tower, 2 notes are missing from the chimes: D# and F were chosen to be left out because they’re not in the university’s fight songs. Need to send a card? A post office is also on the first floor. Just try not to get lost—there are 20 levels in this building!",
                40.10928036555537, -88.2283365726471, false);
        newsstream[19] = new source("Alma Mater", "The statue north of Altgeld Hall is the Alma Mater; it was sculpted by the world-famous sculptor and University of Illinois alumnus Lorado Taft. It represents the university’s motto, \"Learning and Labor,\" and its inscription reads: \"To thy happy children of the future, those of the past send greetings.\" The Alma Mater is the most photographed place on campus and is also one of the most school-spirited. Alma frequently dresses up for special events like homecoming, big games, snow days, and graduation!\n" +
                "\n" + "It seems fitting that Illinois is home to the tradition of Homecoming. Since 1910, one of the earliest recorded Homecoming events took place right here on the Illinois campus. In recent years, homecoming has been enhanced by iHelp, a day during homecoming activities when over 1,000 students complete service work in the community to celebrate!",
                40.10989167977397, -88.22838485240936, false);
        newsstream[20] = new source("Campustown", "Green Street is the main line of Campustown. It’s one of the best and most convenient areas to grab a bite to eat on campus. Restaurants here offer cuisine from all over the world, and multiple stores sell the textbooks you need and the Illini gear you can’t live without!",
                40.1102650300507, -88.23036968708038, false);
        newsstream[21] = new source("Illini Union", "The Illini Union was dedicated as a \"building which would be not only a distinguished social center, but also to inspire those who use it with the best traditions.\" It has definitely accomplished that goal.\n" +
                "\n" +
                "The Union is the most popular student hangout on campus. It contains a food court, bowling alley, and billiard room in the basement. It also houses reading rooms, the McKinley Health Resource Center, the Courtyard Café, an art gallery, Registered Student Organization offices, a branch of the University of Illinois Credit Union, and a hotel. With all that, it’s no wonder an average of 20,000 students enter this building each day!",
                40.10941575776873, -88.22717249393463, false);
        newsstream[22] = new source("Engineering Hall", "Engineering Hall houses the administrative offices for Engineering and other helpful student resources. It's one of the oldest buildings on campus. Talk with the deans located in Room 206 (the 2nd floor). They're eager to answer all your questions about admissions and academics here at Illinois.",
                40.110777870496435, -88.22696328163147, false);
        newsstream[23] = new source("Everitt Lab", "After undergoing a two-year, complete renovation, Everitt Lab will become home to our Bioengineering Department in June 2018. Everitt Lab will provide Bioengineering faculty and students with some of the best research and instructional facilities in the country. This includes our Jump Simulation Center, which the Carle Illinois College of Medicine will use to train a new type of physician innovator uniquely equipped to transform health care.\n" +
                "\n" +
                "Everitt Lab is named for William L. Everitt, former dean of the College of Engineering, who helped transform the college into a research and education powerhouse after World War II.",
                40.11089890027767, -88.22826415300369, false);
        newsstream[24] = new source("Talbot Lab", "Housed in Talbot Lab are the departments of Theoretical and Applied Mechanics (2nd floor) and Aerospace Engineering (3rd floor). This building was built around the 3-million-pound tension and compression testing machine located in the basement. The load frame is 50 feet high and weighs 200,000 pounds.",
                40.11185277152432, -88.22824269533157, false);
        newsstream[25] = new source("Micro & Nanotechnology Lab", "One of the nation's largest and most sophisticated university-based facilities for semiconductor, nanotechnology, and biotechnology research, this building contains over 8,000 square feet for clean room laboratories and state-of-the-art ultra-high-speed optical and electrical device and circuit measurements.",
                40.11393503472304, -88.22788791831624, false);
        newsstream[26] = new source("Electrical & Computer Engineering Building", "Opening its doors in the fall of 2014, this state-of-the-art building was created with the goal of net-zero energy use, meaning that it supplies all of its own energy. It provides the Department of Electrical and Computer Engineering with approximately 230,000 square feet of labs, classrooms, and facilities and focuses on student learning spaces.",
                40.11488304926213, -88.22811735324211, false);
        newsstream[27] = new source("Beckman Institute", "Beckman Institute was made possible by a generous gift from Illinois alumnus Arnold O. Beckman and his wife, Mabel. Since 1989, Beckman has been one of the top interdisciplinary research facilities in the world devoted to leading-edge research in the physical sciences, computation, engineering, biology, behavior, cognition, and neuroscience. The institute is named after Arnold Beckman, who founded Beckman Instruments and invented the first pH meter. Hungry? Beckman has a café open to the public, located on the first floor.\n" +
                "\n" +
                "In addition, Beckman has 2 exhibits on the main floor that showcase the history and conception of the facility. The first exhibit, located in the rotunda, highlights the life of Beckman. He received a bachelor's degree in chemical engineering in 1922 and a master's degree in physical chemistry in 1923. The second exhibit, located in the atrium, displays various instruments designed by Beckman to further advance the fields of science and technology.",
                40.11570763881547, -88.22748166970558, false);
        newsstream[28] = new source("Computer Systems & Research Lab", "This building houses the Coordinate Science Laboratory, where research topics include advanced circuit design, supercomputing, communications, and robotics. In addition, the National Center for Supercomputing Applications and the Center for Supercomputing Research and Development are both housed here.",
                40.114870198702654, -88.22671920061111, false);
        newsstream[29] = new source("Newmark Lab", "Named after Nathan Newmark, who pioneered earthquake resistant buildings here on the Illinois campus, this building houses the Department of Civil and Environmental Engineering and the Yeh Student Center, donated by M.T. Geoffrey Yeh. Yeh graduated in 1953 and continued his success as chairman of a large construction and holding company in Shanghai. The Yeh Student Center includes classrooms, conference rooms, and student study space.\n" +
                "\n" + "Enter through the front door and take the stairs on your left to the 2nd floor, where you can view Crane Bay, the largest lab on campus. You'll find 40-ton and 20-ton cranes, as well as an earthquake tester. The concrete canoes mounted on the wall inside of Crane Bay are part of an annual student design project. Students from any discipline help to design, build, and race the canoes at a regional competition.",
                40.113971753757134, -88.22652071714401, false);
        newsstream[30] = new source("Digital Computer Lab", "This lab was completed in 1989 and constructed around an existing building whose outer walls are visible from inside. The original building held ILLIAC I, one of the world's first supercomputers. The Digital Computer Lab currently houses offices for the Engineering Career Services Center, the Department of Bioengineering, and Technology Services.",
                40.113094860734904, -88.22636650943559, false);
        newsstream[31] = new source("Grainger Engineering Library", "This is the largest and most technologically advanced engineering library in the U.S., and it attracts award-winning faculty from around the world. It has an engineering workstation lab in the basement, reference and resource desks on the main floor, study areas on the 2nd and 3rd floors, and conference and study rooms on the 4th floor.\n" +
                "\n" +
                "Located in the basement of Grainger is an engineering workstation lab available for use by Engineering students. This lab is just one of 8 workstation labs on the Engineering Campus open to students. They have the most up-to-date design software you'll need for your design classes.",
                40.1124460759234, -88.22685360647239, false);
        newsstream[32] = new source("Thomas M. Siebel Center for Computer Science", "Dedicated in 2004, the Thomas M. Siebel Center for Computer Science is the home for the Department of Computer Science at the University of Illinois at Urbana-Champaign. It was made possible by a generous gift from Thomas M. Siebel (MS CS ’85), with additional funding from the State of Illinois and the University of Illinois. The Siebel Center houses more than 225,000 square feet of classrooms, laboratories, and offices. Since it opened its doors, thousands of students have benefited from the educational and research endeavors within its walls.",
                40.11387337265546, -88.22514081718577, false);
        newsstream[33] = new source("Mechanical Engineering Lab", "The Mechanical Engineering Lab originally opened in 1905 and underwent extensive modernization in the early 2000s. It includes several great features—John Deere Pavilion, Ford Design and Manufacturing Lab, McGinnis Studios, Rosenthal Galleries, and a 3rd-floor skylight. Inside you can view Stereolithography, the 3D layering of simple objects, and a display by the EcoIllini, who design prototype vehicles using alternative fuel.",
                40.111735925433436, -88.22613859893931, false);
        newsstream[34] = new source("Material Science & Engineering Building", "This building houses the Material Science and Engineering Department. It's one of the largest departments of its kind in the nation and is currently ranked 2nd in undergraduate studies. Material Science and Engineering was created from Metallurgy and Ceramics Engineering.",
                40.11085992460891, -88.22611570358276, false);
    }
}

