     1  #! /bin/sh
     2
     3  APP_NAME="SonarQube"
     4
     5  # By default, java from the PATH is used, except if SONAR_JAVA_PATH env variable is set
     6  findjava() {
     7    if [ -z "${SONAR_JAVA_PATH}" ]; then
     8      if ! command -v java 2>&1; then
     9        echo "Java not found. Please make sure that the environmental variable SONAR_JAVA_PATH points to a Java executable"
    10        exit 1
    11      fi
    12      JAVA_CMD=java
    13    else
    14      if ! [ -x "${SONAR_JAVA_PATH}" ] || ! [ -f "${SONAR_JAVA_PATH}" ]; then
    15        echo "File '${SONAR_JAVA_PATH}' is not executable. Please make sure that the environmental variable SONAR_JAVA_PATH points to a Java executable"
    16        exit 1
    17      fi
    18      JAVA_CMD="${SONAR_JAVA_PATH}"
    19    fi
    20  }
    21
    22  findjava
    23
    24  # Get the fully qualified path to the script
    25  case $0 in
    26      /*)
    27          SCRIPT="$0"
    28          ;;
    29      *)
    30          PWD=`pwd`
    31          SCRIPT="$PWD/$0"
    32          ;;
    33  esac
    34
    35  # Resolve the true real path without any sym links.
    36  CHANGED=true
    37  while [ "X$CHANGED" != "X" ]
    38  do
    39      # Change spaces to ":" so the tokens can be parsed.
    40      SAFESCRIPT=`echo $SCRIPT | sed -e 's; ;:;g'`
    41      # Get the real path to this script, resolving any symbolic links
    42      TOKENS=`echo $SAFESCRIPT | sed -e 's;/; ;g'`
    43      REALPATH=
    44      for C in $TOKENS; do
    45          # Change any ":" in the token back to a space.
    46          C=`echo $C | sed -e 's;:; ;g'`
    47          REALPATH="$REALPATH/$C"
    48          # If REALPATH is a sym link, resolve it.  Loop for nested links.
    49          while [ -h "$REALPATH" ] ; do
    50              LS="`ls -ld "$REALPATH"`"
    51              LINK="`expr "$LS" : '.*-> \(.*\)$'`"
    52              if expr "$LINK" : '/.*' > /dev/null; then
    53                  # LINK is absolute.
    54                  REALPATH="$LINK"
    55              else
    56                  # LINK is relative.
    57                  REALPATH="`dirname "$REALPATH"`""/$LINK"
    58              fi
    59          done
    60      done
    61
    62      if [ "$REALPATH" = "$SCRIPT" ]
    63      then
    64          CHANGED=""
    65      else
    66          SCRIPT="$REALPATH"
    67      fi
    68  done
    69
    70  # Change the current directory to the location of the script
    71  cd "`dirname "$REALPATH"`"
    72
    73  LIB_DIR="../../lib"
    74
    75  HAZELCAST_ADDITIONAL="--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED \
    76  --add-opens=java.base/java.lang=ALL-UNNAMED \
    77  --add-opens=java.base/java.nio=ALL-UNNAMED \
    78  --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
    79  --add-opens=java.management/sun.management=ALL-UNNAMED \
    80  --add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED"
    81
    82  # Sonar app launching process memory setting
    83  XMS="-Xms8m"
    84  XMX="-Xmx32m"
    85
    86  COMMAND_LINE="$JAVA_CMD $XMS $XMX $HAZELCAST_ADDITIONAL -jar $LIB_DIR/sonar-application-9.6.1.59531.jar"
    87
    88  # Location of the pid file.
    89  PIDFILE="./$APP_NAME.pid"
    90
    91  # Resolve the location of the 'ps' command
    92  PSEXE="/usr/bin/ps"
    93  if [ ! -x "$PSEXE" ]
    94  then
    95      PSEXE="/bin/ps"
    96      if [ ! -x "$PSEXE" ]
    97      then
    98          echo "Unable to locate 'ps'."
    99          echo "Please report this message along with the location of the command on your system."
   100          exit 1
   101      fi
   102  fi
   103
   104  getpid() {
   105      if [ -f "$PIDFILE" ]
   106      then
   107          if [ -r "$PIDFILE" ]
   108          then
   109              pid=`cat "$PIDFILE"`
   110              if [ "X$pid" != "X" ]
   111              then
   112                  # It is possible that 'a' process with the pid exists but that it is not the
   113                  #  correct process.  This can happen in a number of cases, but the most
   114                  #  common is during system startup after an unclean shutdown.
   115                  # The ps statement below looks for the specific wrapper command running as
   116                  #  the pid.  If it is not found then the pid file is considered to be stale.
   117                  pidtest=`$PSEXE -p $pid -o args | grep "sonar-application-9.6.1.59531.jar" | tail -1`
   118                  if [ "X$pidtest" = "X" ]
   119                  then
   120                      # This is a stale pid file.
   121                      rm -f "$PIDFILE"
   122                      echo "Removed stale pid file: $PIDFILE"
   123                      pid=""
   124                  fi
   125              fi
   126          else
   127              echo "Cannot read $PIDFILE."
   128              exit 1
   129          fi
   130      fi
   131  }
   132
   133  testpid() {
   134      pid=`$PSEXE -p $pid | grep $pid | grep -v grep | awk '{print $1}' | tail -1`
   135      if [ "X$pid" = "X" ]
   136      then
   137          # Process is gone so remove the pid file.
   138          rm -f "$PIDFILE"
   139          pid=""
   140      fi
   141  }
   142
   143  console() {
   144      echo "Running $APP_NAME..."
   145      getpid
   146      if [ "X$pid" = "X" ]
   147      then
   148          echo $$ > $PIDFILE
   149          exec $COMMAND_LINE -Dsonar.log.console=true
   150      else
   151          echo "$APP_NAME is already running."
   152          exit 1
   153      fi
   154  }
   155
   156  start() {
   157      echo "Starting $APP_NAME..."
   158      getpid
   159      if [ "X$pid" = "X" ]
   160      then
   161          exec nohup $COMMAND_LINE >../../logs/nohup.log 2>&1 &
   162          echo $! > $PIDFILE
   163      else
   164          echo "$APP_NAME is already running."
   165          exit 1
   166      fi
   167      getpid
   168      if [ "X$pid" != "X" ]
   169      then
   170          echo "Started $APP_NAME."
   171      else
   172          echo "Failed to start $APP_NAME."
   173      fi
   174  }
   175
   176  waitforstop() {
   177      savepid=$pid
   178      CNT=0
   179      TOTCNT=0
   180      while [ "X$pid" != "X" ]
   181      do
   182          # Show a waiting message every 5 seconds.
   183          if [ "$CNT" -lt "5" ]
   184          then
   185              CNT=`expr $CNT + 1`
   186          else
   187              echo "Waiting for $APP_NAME to exit..."
   188              CNT=0
   189          fi
   190          TOTCNT=`expr $TOTCNT + 1`
   191
   192          sleep 1
   193
   194          testpid
   195      done
   196
   197      pid=$savepid
   198      testpid
   199      if [ "X$pid" != "X" ]
   200      then
   201          echo "Failed to stop $APP_NAME."
   202          exit 1
   203      else
   204          echo "Stopped $APP_NAME."
   205      fi
   206  }
   207
   208  stopit() {
   209      echo "Gracefully stopping $APP_NAME..."
   210      getpid
   211      if [ "X$pid" = "X" ]
   212      then
   213          echo "$APP_NAME was not running."
   214      else
   215          kill $pid
   216          if [ $? -ne 0 ]
   217          then
   218              # An explanation for the failure should have been given
   219              echo "Unable to stop $APP_NAME."
   220              exit 1
   221          fi
   222
   223          waitforstop
   224      fi
   225  }
   226
   227  forcestopit() {
   228      getpid
   229      if [ "X$pid" = "X" ]
   230      then
   231          echo "$APP_NAME not running"
   232          exit 1
   233      fi
   234
   235      testpid
   236      if [ "X$pid" != "X" ]
   237      then
   238        # start shutdowner from SQ installation directory
   239        cd "../.."
   240
   241        echo "Force stopping $APP_NAME..."
   242        ${JAVA_CMD} -jar "lib/sonar-shutdowner-9.6.1.59531.jar"
   243
   244        waitforstop
   245      fi
   246  }
   247
   248  status() {
   249      getpid
   250      if [ "X$pid" = "X" ]
   251      then
   252          echo "$APP_NAME is not running."
   253          exit 1
   254      else
   255          echo "$APP_NAME is running ($pid)."
   256          exit 0
   257      fi
   258  }
   259
   260  dump() {
   261      echo "Dumping $APP_NAME..."
   262      getpid
   263      if [ "X$pid" = "X" ]
   264      then
   265          echo "$APP_NAME was not running."
   266
   267      else
   268          kill -3 $pid
   269
   270          if [ $? -ne 0 ]
   271          then
   272              echo "Failed to dump $APP_NAME."
   273              exit 1
   274          else
   275              echo "Dumped $APP_NAME."
   276          fi
   277      fi
   278  }
   279
   280  case "$1" in
   281
   282      'console')
   283          console
   284          ;;
   285
   286      'start')
   287          start
   288          ;;
   289
   290      'stop')
   291          stopit
   292          ;;
   293
   294      'force-stop')
   295          forcestopit
   296          ;;
   297
   298      'restart')
   299          stopit
   300          start
   301          ;;
   302
   303      'status')
   304          status
   305          ;;
   306
   307      'dump')
   308          dump
   309          ;;
   310
   311      *)
   312          echo "Usage: $0 { console | start | stop | force-stop | restart | status | dump }"
   313          exit 1
   314          ;;
   315  esac
   316
   317  exit 0
