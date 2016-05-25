from django import forms

class UploadDatasetForm(forms.Form):
    file = forms.FileField()
    algorithm = forms.ChoiceField([('STSC', 'STSC'), ('OPTICS', 'OPTICS')], widget=forms.RadioSelect())
