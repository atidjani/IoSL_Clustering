from django import forms

class UploadDatasetForm(forms.Form):
    file = forms.FileField()
    algorithm = forms.ChoiceField([('STSC', 'STSC'), ('OPTICS', 'OPTICS')], widget=forms.RadioSelect())

class ParametersSTSC(forms.Form):
    numClusters = forms.DecimalField(required=True,min_value=1)
    k = forms.DecimalField(required=True,min_value=0)
