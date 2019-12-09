import csv
import datetime

with open('on_time_performance_2019_09.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    f = open("formattedData.txt", "a")
    line_count = 0
    for row in csv_reader:
        if not line_count == 0:
            print(row)
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
            dow = date.weekday();
            stringLine +=  " " +  str(dow * 1000000000)     # Day Of Week
            stringLine += "\n"
            print(stringLine)
        line_count += 1

    print('Processed {line_count} lines.')