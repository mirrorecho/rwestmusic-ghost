from abjad import *
import settings
from calliope.tools import music_from_durations

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

    def get_music(self):
        section_staff = abjad.scoretools.Staff()
        for l in self.lines:
            section_staff.extend(l.get_music())
        return section_staff

class Event:
    """
    represents the choice of a given pitch/rhythm cell combination at a particular point in the score, 
    several events in a row make a line...
    """
    def __init__(self, ghost):
        self.ghost = ghost
        self.material_names = [] # names of both pitch and rhythm material
        self.music = None
        self.length = None

    def cell_by_type(self, cell_type):
        for n in material_names:
            if self.ghost.cells[n].cell_type=cell_type:
                return self.ghost.cells[n]

    def length(self):
        return my_event.cell_by_type("rhythm").length

    def get_music(self):
        """
        returns abjad music Container for this event
        """
        return music_from_durations(
            durations = cell_by_type("rhythm").material,
            pitches = cell_by_type("pitch").material
            ))=

class Line:
    def __init__(self, name, section, ref_pitches, length=1):
        self.events = []
        self.ref_pitches = ref_pitches
        self.section = section
        self.name = name
        self.length = 1 # maybe poor naming? could be confused with event array length?

    def events_length(self):
        return sum([e.length() for e in self.events])

    def current_index(self):
        return len(self.events) - 1

    def get_event(self, back_index=0):
        if self.current is not None:
            return self.events[self.current_index() + back_index]

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
        if self.length - self.events_length >= self.section.ghost.min_event_length:

            my_event = Event(ghost=self.section.ghost)
            self.events.append(my_event)

            first_material_type =  random.choice(["pitch","rhythm"])
            my_event.material_names.append(self.choose_for_type(first_material_type).name)

            second_material_type = "pitch" if first_material_type = "rhythm" else "rhythm"
            my_event.material_names.append(self.choose_for_type(second_material_type).name)   
        else:
            return None

    def get_music(self):
        line_staff = scoretools.Staff()
        while self.next:
            line_staff.extend(self.get_event().get_music())
        return line_staff



class Cell:
    """
    defines material as either a row of pitches, or a rhythm (rhythm includes articulations and dynamics)... 
    along with weights for probability of where this 
    material may occur in the overall structure of the piece
    """
    def __init__(self, ghost, name, cell_type="pitch", material=None, length=0.25, *args, **kwargs):
        self.name = name
        self.cell_type = "pitch"
        self.material = material # either a list of pitches, or a list of c notes (with rhythm, articulations, dynamics)
        self.weights = []
        if self.cell_type = "rhythm":
            self.length = length

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

    """
    #TO DO:
    # - able to output actual music
    # - test probabilities
    # - test actual music output
    # - develop possible pitch / rhythm material... generate and curate
    # - actually prevent overflow of cell lengths?
    # - work out numbered sections in relation to electronic cued sections
    # - printable formatted score that works for performance along with electronics
    # - show electronics flourishes, with cued cells
    # - final development of pitch / rhythm material... generate and curate


    def __init__(self, min_event_length=0.25):
        self.cells = {} # each cell is a named list of notes
        self.min_event_length = 0.25
        self.sections = []

    def add_section(self, *args, **kwargs):
        self.sections.append(Section(*args, **kwargs))

    def add_cell(self, name, *args, **kwargs):
        self.cells[name] = Cell(*args, **kwargs)

    def add_weight(self, cell_name, *args, **kwargs):
        self.cells[cell_name].add_weight(*args, **kwargs)

    def get_music(self):
        ghost_staff = scoretools.Staff()
        for s in self.sections:
            ghost_staff.extend(s.get_music())
        return ghost_staff

    def get_score(self):
        score = scoretools.Score()
        score.append(self.get_music())
        return score


