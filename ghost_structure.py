from abjad import *

abjad_configuration.set_default_accidental_spelling('flats')

score = Score([])

flute_staff = scoretools.Staff()

class Ghost:
    #to do... just add any cell... (by name)
    def __init__(self):
        self.pitches = [1, 0, 5, 8, 7, 1, 3, 6, 5, 0]
        self.cells = [[0,1], [3,4], [6,7,8]]
        self.holds = [2, 5, 9]
        self.notes = []

    def add_cell(self, cell_index, durations=[(1,8)]):
        append_notes = [scoretools.Note(self.pitches[pitch_index], durations[0]) for pitch_index in self.cells[cell_index]]
        self.notes.extend(append_notes)

ghost1 = Ghost()
ghost1.add_cell(0, [(1,4)])
ghost1.add_cell(0)

# to do... output notes as JSON data

flute_staff.extend(ghost1.notes)

score.append(flute_staff)

show(score)