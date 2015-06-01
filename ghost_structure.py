from abjad import *
from collections import OrderedDict
import random


abjad_configuration.set_default_accidental_spelling('flats')

score = Score([])

flute_staff = scoretools.Staff()

# cell = name, pitches

p = [ ("p_stitch"), ("r_yoyo",-1) ]

class CellWeight:
    def __init__(self, weight, sections=None, ref_pitches=None, combos=None):
        self.weight = weight
        self.sections = sections
        self.ref_pitches = ref_pitches
        self.combos = combos

    def weight_applies(self, line):
        if self.sections is not None and line.section not in self.sections:
            return False
        if self.ref_pitches is not None and not any(p in line.ref_pitches for p in self.ref_pitches):
            return False
        if self.combos is not None:
            for combo in self.combos:
                back_index = 0 if len(combo) = 1 else combo[1]
                if (line.current_index + back_index) >= 0 and combo[0] not in line.get_event(back_index).material_names:
                    return False
        return True


class Section:
    """
    main sections that define the overall form of the piece. (e.g. A, B, C)
    """
    def __init__(self, ghost, name="_", first_line=1, start_intervals=[0,1,2,3,4,5], double_intervals=[0,6]):
        self.lines = []
        self.ghost = ghost
        for i in start_intervals:
            self.lines.append( Line(
                str(i+first_line) 
                section=self, 
                ref_pitches=[(d + i) % 12 for d in double_intervals] 
                ))

class Event:
    """
    represents the choice of a given pitch/rhythm cell combination at a particular point in the score, 
    several events in a row make a line...
    """
    def __init__(self):
        self.material_names = [] # names of both pitch and rhythm material
        self.music = None

    def choose_cells(self):
        pass

    def get_music(self):
        """
        for debugging use only... 
        """
        pass

class Line:
    def __init__(self, name, section, ref_pitches):
        self.events = []
        self.ref_pitches = ref_pitches
        self.section = section
        self.name = name
        self.current_index = None

    def current_index(self):
        return len(self.events) - 1

    def get_event(self, back_index=0):
        if self.current is not None:
            return self.events[self.current_index + back_index]

    def choose_for_type(self, cell_type):
        cells = [c for c in self.section.ghost.cells if c.cell_type = cell_type]
        cell_weights = [c.weight_for_line(self) for c in cells]
        selection = random.uniform(0,sum(cell_weights))

        weight_counter = 0
        for i in range(len(cells)):
            weight_counter += cell_weights[i]
            if sum(cell_weights[: i + 1]) >= weight_counter:
                return cells[i]


    def next(self):
        my_event = Event()
        self.events.append(my_event)

        first_material_type =  random.choice(["pitch","rhythm"])
        my_event.material_names.append(self.choose_for_type(first_material_type).name)

        second_material_type = "pitch" if first_material_type = "rhythm" else "rhythm"
        my_event.material_names.append(self.choose_for_type(second_material_type).name)        



class Cell:
    """
    defines material as either a row of pitches, or a rhythm (rhythm includes articulations and dynamics)... 
    along with weights for probability of where this 
    material may occur in the overall structure of the piece
    """
    def __init__(self, ghost, name, cell_type="pitch", material=None, default_weight=4, *args, **kwargs):
        self.name = name
        self.cell_type = "pitch"
        self.material = material # either a list of pitches, or a list of c notes (with rhythm, articulations, dynamics)
        self.weights = []

        # for s_name in ghost.sections:
        #     self.weights.sections[s_name] = default_weight
        # for r_name in ghost.refs:
        #     self.weights.refs[r_name] = default_weight
        # for c_name, cell in ghost.cells.items(): #this or enumerate?
        #     for i in range(ghost.markov_order + 1):
        #         # add weight default to this cell for other cell:
        #         self.weights.combos[i][c_name] = default_weight
        #         # add weight default to other cell for this cell:
        #         cell.weights.combos[i][name] = default_weight

    def add_weight(self, *args, **kwargs):
        self.weights.append(CellWeight(*args, **kwargs))

    def weight_for_line(self, line):
        """
        returns the weight to be given this cell (on scale of 0 to 1)
        for a speficied line
        """
        applies_at_all = 0
        applied_weights = 1
        for w in self.weights:
            if w.weight_applies(line)
                applies_at_all = 1
                applied_weights *= w.weight
        return applies_at_all * applied_weights



class Ghost:
    """
    define cells... defined with:
    - pitches or rhythms
    - weight for occuring during a given perfomance section
    - weight for occuring during a given ref pitch
    - weight for occuring alongisde pitch/rhythm
    - weight for occuring following prev cell (both pitch and rhythm)
    - weight for occuring following prev-prev cell (both pitch and rhythm)
    rhythm cell ... defined with:
    - 
    """
    #TO DO:
    # - define cell pitch / rhythm combination probabilities
    # - define pitch cell probability in terms of electronic ref pitch
    # - define rhythm cell probability in terms of beginning/middle/end
    # - define pitch / rhythm cell probability in terms of markov chain
    # - further develop possible pitch / rhythm material... generate and curate
    # - work out numbered sections in relation to electronic cued sections
    # - printable formatted score that works for performance along with electronics
    # - show electronics flourishes, with cued cells
    # - final development of pitch / rhythm material... generate and curate


    def __init__(self, markov_order=2):
        self.cells = {} # each cell is a named list of notes
        self.markov_order = markov_order
        self.sections = []

    def add_section(self, *args, **kwargs):
        self.sections.append(Section(*args, **kwargs))

    def add_cell(self, name, *args, **kwargs):
        self.cells[name] = Cell(*args, **kwargs)

    def add_weight(self, cell_name, *args, **kwargs):
        self.cells[cell_name].add_weight(*args, **kwargs)

    # def define_cell(self, cell_name, cell_ly):
    #     self.cells[cell_name] = [Note(note) for note in cell_ly.split()]

    # def add_cell(self, cell_name, transposition=0, durations=[]):
    #     #append_notes = [scoretools.Note(self.pitches[pitch_index], durations[0]) for pitch_index in self.cells[cell_index]]
    #     #self.notes.extend(append_notes)
    #     self.notes.extend(self.cells[cell_name])


    def get_music(self):
        pass


# ghost1 = Ghost()
# ghost1.define_cell('moan', "gf'8 f'4.")
# # ghost1.define_cell('moan', "gf'8 f'4.")

# ghost1.slur_cell('moan')
# ghost1.add_cell('moan')

# # to do... output notes as JSON data

# flute_staff.extend(ghost1.notes)


# score.append(flute_staff)


# show(score)