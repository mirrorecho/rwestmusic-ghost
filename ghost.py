from abjad import *
from ghost_structure import Ghost

g = Ghost()

g.add_section(name="A", first_line=1, start_intervals=[0,1,2,6,7,8], double_intervals=[0,3])
g.add_section(name="B", first_line=7, start_intervals=[0,1,2,3,4,5], double_intervals=[0,6])
g.add_section(name="C", first_line=13, start_intervals=[0,1,4,5,8,9], double_intervals=[0,2])

g.add_cell("p_ghost1", cell_type="pitch", material=["Gb4","F4","D4","Eb4","C4"])
g.add_cell("p_ghost2", cell_type="pitch", material=["Gb4","F4","D4","Eb4","F4"])

g.add_cell("r_2holds", cell_type="rhythm", material="c1\\fermata c1\\fermata")
g.add_cell("r_straight", cell_type="rhythm", material="c4-- c4-- c4-- c4--")

g.add_weight("p_ghost1", 0.4, sections=("A",))
g.add_weight("p_ghost2", 0.4)

g.add_weight("r_straight", 0.4)
g.add_weight("r_2holds", 0.4, sections=("A","C",) )
g.add_weight("r_2holds", 0, combos=(("r_2holds",-1), ) ) # except never repeat 2holds twice in a row

score = g.get_score()
# print(score[0][0])
show(score)