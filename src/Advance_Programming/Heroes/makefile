CC = g++
COMP_FLAG = -c
OBJS = GameException.o Hero.o Warrior.o Thief.o Necromancer.o Creature.o Zombie.o Archer.o Vampire.o Wizard.o BlackDragon.o main.o
EXEC = Heroes

$(EXEC) : $(OBJS)
	$(CC) $(OBJS) -o $@

GameException.o : GameException.h
	$(CC) $(COMP_FLAG) $*.cpp

Creature.o : Creature.h GameException.h
	$(CC) $(COMP_FLAG) $*.cpp

Hero.o : Hero.h Creature.h GameException.h
	$(CC) $(COMP_FLAG) $*.cpp

Warrior.o : Warrior.h Hero.h
	$(CC) $(COMP_FLAG) $*.cpp

Thief.o : Thief.h Hero.h
	$(CC) $(COMP_FLAG) $*.cpp

Necromancer.o : Necromancer.h Hero.h
	$(CC) $(COMP_FLAG) $*.cpp

Zombie.o : Creature.h Zombie.h Archer.h
	$(CC) $(COMP_FLAG) $*.cpp

Archer.o : Creature.h Archer.h BlackDragon.h
	$(CC) $(COMP_FLAG) $*.cpp

Vampire.o : Creature.h Vampire.h
	$(CC) $(COMP_FLAG) $*.cpp

Wizard.o : Creature.h Wizard.h
	$(CC) $(COMP_FLAG) $*.cpp

BlackDragon.o : Creature.h BlackDragon.h Wizard.h
	$(CC) $(COMP_FLAG) $*.cpp

main.o : main.cpp Warrior.h Thief.h Necromancer.h
	$(CC) $(COMP_FLAG) $*.cpp

clean :
	rm -f $(OBJS) $(EXEC)
