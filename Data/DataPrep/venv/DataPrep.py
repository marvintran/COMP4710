import csv
import datetime
import time

filename =  'on_time_performance_2019_09.csv'
print("Please enter filename (with extention)")
filename = input()
lastUpdate = time.time()
with open(filename.strip()) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    f = open("formattedData.txt", "a")
    line_count = 0
    for row in csv_reader:
        if not line_count == 0:
            if(time.time() - lastUpdate > 5):#Print an update each 30 secs so users know it's NOT broken.
                print("Working Row %d"%(line_count))
                lastUpdate = time.time()
            stringLine = row[1];                    #Route #
            stringLine += " " + str(int(row[2]) * 100000);#Bus # (Shifted 5 over)
            hour = int((row[6].split('T'))[1].split(':')[0])
            tod = 5;
            if hour > 4:
                tod = 1
            if hour > 10:
                tod = 2
            if hour > 14:
                tod = 3
            if hour > 18:
                tod = 4
            if hour > 22:
                tod = 5
            stringLine +=  " " + str(tod * 100000000)
            # Time Of Day   Mappings
            # 1 Morning     4   10
            # 2 LunchTime   10  2pm
            # 3 AfterNoon   2   6
            # 4 Evning      6   10
            # 5 Night       10  4am
            dateTok = (row[6].split('T'))[0].split('-')
            date = datetime.datetime(int(dateTok[0]),int(dateTok[1]),int(dateTok[2]))
            dow = date.weekday() + 1;
            stringLine +=  " " +  str(dow * 1000000000)     # Day Of Week
            # Day Of Week Mappings
            # 1 Monday
            # 2 Tuesday
            # 3 Wednesday
            # 4 Thursday
            # 5 Friday
            # 6 Saturday
            # 7 Sunday
            delayVal = int(row[7])
            delay = 7
            if (delayVal < (60 * 30)):
                delay = 6
            if (delayVal < (60 * 10)):
                delay  = 5
            if (delayVal < (60 * 3)):
                delay  = 4
            if (delayVal < (-60 * 3)):
                delay  = 3
            if (delayVal < (-60 * 10)):
                delay  = 2
            if (delayVal < (-60 * 20)):
                delay  = 1
            # Delay Mappings
            # 1 Way too early   < (-60 * 20)
            # 2 very early      < (-60 * 10)
            # 3 early           < (-60 * 3)
            # 4 on time         < (60 * 3)
            # 5 late            < (60 * 10)
            # 6 very Late       < (60 * 30)
            # 7 NOSHOW          > (60 * 60)
            stringLine += " " + str(delay)  # Delay
            stringLine += "\n"
            #print(stringLine)
            f.write(stringLine);
        line_count += 1

    print('Processed %d lines.'%(line_count))