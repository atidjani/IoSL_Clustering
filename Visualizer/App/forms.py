from django import forms

class UploadDatasetForm(forms.Form):
    file = forms.FileField(label='')
    algorithm = forms.ChoiceField([('STSC', 'STSC'), ('OPTICS', 'OPTICS')], widget=forms.RadioSelect(), label='')

class ParametersSTSC(forms.Form):
    numClusters = forms.DecimalField(required=True,min_value=1,label='# Clusters')
    k = forms.DecimalField(required=True,min_value=0)
