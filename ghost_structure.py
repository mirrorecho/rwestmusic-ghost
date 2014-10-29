from abjad import *

abjad_configuration.set_default_accidental_spelling('flats')

score = Score([])

flute_staff = scoretools.Staff()

# cell = name, pitches

class Ghost:
    #to do... just add any cell... (by name)
    def __init__(self):
        self.cells = {} # each cell is a named list of notes
        self.notes = []

    def slur_cell(self, cell_name):
        attach(Slur(), self.cells[cell_name])

    def define_cell(self, cell_name, cell_ly):
        self.cells[cell_name] = [Note(note) for note in cell_ly.split()]

    def add_cell(self, cell_name, transposition=0, durations=[]):
        #append_notes = [scoretools.Note(self.pitches[pitch_index], durations[0]) for pitch_index in self.cells[cell_index]]
        #self.notes.extend(append_notes)
        self.notes.extend(self.cells[cell_name])

ghost1 = Ghost()
ghost1.define_cell('moan', "gf'8 f'4.")
ghost1._cell('moan', "gf'8 f'4.")

ghost1.slur_cell('moan')
ghost1.add_cell('moan')



# to do... output notes as JSON data

flute_staff.extend(ghost1.notes)

score.append(flute_staff)

show(score)