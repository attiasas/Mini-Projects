CC = g++
COMP_FLAG = -c
OBJS = Employee.o DataBase.o main.o
EXEC = run

$(EXEC) : $(OBJS)
	$(CC) $(OBJS) -o $@

Employee.o : Employee.h
	$(CC) $(COMP_FLAG) $*.cpp

DataBase.o : DataBase.h Employee.h
	$(CC) $(COMP_FLAG) $*.cpp

main.o : main.cpp Employee.h DataBase.h
	$(CC) $(COMP_FLAG) $*.cpp

clean :
	rm -f $(OBJS) $(EXEC)
